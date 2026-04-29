package com.biblio.view;

import com.biblio.controller.ReferenceController;
import com.biblio.model.Etiquette;
import com.biblio.model.Reference;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panneau de gestion des références avec recherche par étiquette.
 * Appelle la boîte de dialogue {@link ReferenceDialog} pour l'ajout et la
 * modification, et rafraîchit correctement le tableau via
 * {@code fireTableDataChanged()} après chaque opération (correctif J11).
 *
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class ReferencePanel extends JPanel {
    private final ReferenceController controller = new ReferenceController();
    private final RefTableModel model = new RefTableModel();
    private final JTable table = new JTable(model);
    private final JComboBox<String> cbEtiquettes = new JComboBox<>();

    public ReferencePanel() {
        setLayout(new BorderLayout(5, 5));

        // --- Barre de recherche ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Rechercher par étiquette :"));
        top.add(cbEtiquettes);
        JButton btFiltrer = new JButton("Filtrer");
        JButton btReset = new JButton("Réinitialiser");
        top.add(btFiltrer);
        top.add(btReset);
        add(top, BorderLayout.NORTH);

        // --- Tableau ---
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Boutons d'action ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btAjouter = new JButton("Ajouter");
        JButton btModifier = new JButton("Modifier");
        JButton btSupprimer = new JButton("Supprimer");
        JButton btRefresh = new JButton("Rafraîchir");
        bottom.add(btAjouter);
        bottom.add(btModifier);
        bottom.add(btSupprimer);
        bottom.add(btRefresh);
        add(bottom, BorderLayout.SOUTH);

        btFiltrer.addActionListener(e -> filtrer());
        btReset.addActionListener(e -> rafraichir());
        btRefresh.addActionListener(e -> {
            rafraichir();
            chargerEtiquettes();
        });
        btAjouter.addActionListener(e -> ajouter());
        btModifier.addActionListener(e -> modifier());
        btSupprimer.addActionListener(e -> supprimerSelection());

        rafraichir();
        chargerEtiquettes();
    }

    private void chargerEtiquettes() {
        try {
            cbEtiquettes.removeAllItems();
            for (Etiquette e : controller.listerEtiquettes())
                cbEtiquettes.addItem(e.getNom());
        } catch (SQLException ex) {
            erreur(ex);
        }
    }

    private void rafraichir() {
        try {
            model.setData(controller.listerReferences());
        } catch (SQLException e) {
            erreur(e);
        }
    }

    private void filtrer() {
        Object sel = cbEtiquettes.getSelectedItem();
        if (sel == null) return;
        try {
            model.setData(controller.rechercherParEtiquette(sel.toString()));
        } catch (SQLException e) {
            erreur(e);
        }
    }

    private void ajouter() {
        ReferenceDialog d = new ReferenceDialog(SwingUtilities.getWindowAncestor(this),
                controller, null);
        d.setVisible(true);
        if (d.isValidated()) rafraichir();
    }

    private void modifier() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionner une référence.");
            return;
        }
        Reference r = model.getRef(row);
        ReferenceDialog d = new ReferenceDialog(SwingUtilities.getWindowAncestor(this),
                controller, r);
        d.setVisible(true);
        if (d.isValidated()) rafraichir();
    }

    private void supprimerSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = model.getRef(row).getIdReference();
        int c = JOptionPane.showConfirmDialog(this, "Supprimer cette référence ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            controller.supprimerReference(id);
            rafraichir();
        } catch (SQLException e) {
            erreur(e);
        }
    }

    private void erreur(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    /** Modèle de tableau pour les références. */
    private static class RefTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Titre", "Revue", "Année", "Auteurs", "Étiquettes"};
        private List<Reference> data = java.util.Collections.emptyList();

        void setData(List<Reference> d) {
            this.data = d;
            fireTableDataChanged();     // correctif : nécessaire pour rafraîchir le JTable
        }

        Reference getRef(int r) { return data.get(r); }

        public int getRowCount() { return data.size(); }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int c) { return cols[c]; }
        public boolean isCellEditable(int r, int c) { return false; }

        public Object getValueAt(int r, int c) {
            Reference ref = data.get(r);
            switch (c) {
                case 0: return ref.getIdReference();
                case 1: return ref.getTitre();
                case 2: return ref.getRevue();
                case 3: return ref.getAnnee();
                case 4: return String.join(", ", ref.getAuteurs().stream()
                        .map(Object::toString).toArray(String[]::new));
                case 5: return String.join(", ", ref.getEtiquettes().stream()
                        .map(Object::toString).toArray(String[]::new));
                default: return "";
            }
        }
    }
}
