package com.biblio.view;

import com.biblio.controller.ReferenceController;
import com.biblio.model.Auteur;
import com.biblio.model.Etiquette;
import com.biblio.model.Reference;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Boîte de dialogue d'ajout/modification d'une référence bibliographique.
 * Permet de saisir les métadonnées, de sélectionner plusieurs auteurs et
 * plusieurs étiquettes, et applique les règles de validation côté contrôleur.
 *
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class ReferenceDialog extends JDialog {

    private final ReferenceController controller;
    private final Reference reference;       // null = création ; sinon = modification
    private boolean validated = false;

    private final JTextField tfTitre = new JTextField(30);
    private final JTextField tfRevue = new JTextField(30);
    private final JTextField tfAnnee = new JTextField(6);
    private final JTextField tfLien = new JTextField(30);
    private final JTextArea taDesc = new JTextArea(3, 30);
    private final JList<Auteur> listAuteurs = new JList<>();
    private final JList<Etiquette> listEtiquettes = new JList<>();

    public ReferenceDialog(Window owner, ReferenceController controller, Reference existing) {
        super(owner, existing == null ? "Nouvelle référence" : "Modifier la référence",
                ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.reference = existing;
        buildUi();
        chargerListes();
        if (existing != null) preremplir(existing);
        pack();
        setLocationRelativeTo(owner);
    }

    // ---------- UI ----------
    private void buildUi() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;
        addRow(form, c, y++, "Titre* :", tfTitre);
        addRow(form, c, y++, "Revue :", tfRevue);
        addRow(form, c, y++, "Année* :", tfAnnee);
        addRow(form, c, y++, "Hyperlien :", tfLien);

        c.gridx = 0; c.gridy = y; c.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Description :"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        taDesc.setLineWrap(true); taDesc.setWrapStyleWord(true);
        form.add(new JScrollPane(taDesc), c);
        y++;

        // Listes à sélection multiple
        listAuteurs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listEtiquettes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel listes = new JPanel(new GridLayout(1, 2, 10, 0));
        listes.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        listes.add(wrapList("Auteurs* (Ctrl+clic = multiple)", listAuteurs));
        listes.add(wrapList("Étiquettes", listEtiquettes));

        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = 1;
        form.add(listes, c);

        JButton btOk = new JButton("Valider");
        JButton btCancel = new JButton("Annuler");
        btOk.addActionListener(e -> valider());
        btCancel.addActionListener(e -> dispose());

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        boutons.add(btOk); boutons.add(btCancel);

        setLayout(new BorderLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(form, BorderLayout.CENTER);
        add(boutons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btOk);
    }

    private static void addRow(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridx = 0; c.gridy = y; c.fill = GridBagConstraints.NONE; c.weightx = 0;
        p.add(new JLabel(label), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        p.add(field, c);
    }

    private static JComponent wrapList(String titre, JList<?> list) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.add(new JLabel(titre), BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private void chargerListes() {
        try {
            DefaultListModel<Auteur> ma = new DefaultListModel<>();
            for (Auteur a : controller.listerAuteurs()) ma.addElement(a);
            listAuteurs.setModel(ma);

            DefaultListModel<Etiquette> me = new DefaultListModel<>();
            for (Etiquette e : controller.listerEtiquettes()) me.addElement(e);
            listEtiquettes.setModel(me);
        } catch (SQLException ex) {
            erreur(ex);
        }
    }

    private void preremplir(Reference r) {
        tfTitre.setText(r.getTitre());
        tfRevue.setText(r.getRevue());
        tfAnnee.setText(String.valueOf(r.getAnnee()));
        tfLien.setText(r.getHyperlien());
        taDesc.setText(r.getDescription());
        // Pré-sélection des auteurs/étiquettes déjà associés
        selectionner(listAuteurs, r.getAuteurs(), a -> ((Auteur) a).getIdAuteur());
        selectionner(listEtiquettes, r.getEtiquettes(), e -> ((Etiquette) e).getIdEtiquette());
    }

    @SuppressWarnings("unchecked")
    private <T> void selectionner(JList<T> list, List<?> selection, java.util.function.ToIntFunction<Object> idOf) {
        if (selection == null || selection.isEmpty()) return;
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        for (Object o : selection) ids.add(idOf.applyAsInt(o));
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < list.getModel().getSize(); i++) {
            T elt = list.getModel().getElementAt(i);
            if (ids.contains(idOf.applyAsInt(elt))) indices.add(i);
        }
        int[] arr = new int[indices.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = indices.get(i);
        list.setSelectedIndices(arr);
    }

    // ---------- Validation et enregistrement ----------
    private void valider() {
        try {
            int annee;
            try { annee = Integer.parseInt(tfAnnee.getText().trim()); }
            catch (NumberFormatException ex) {
                throw new IllegalArgumentException("L'année doit être un nombre entier.");
            }
            List<Auteur> auteurs = listAuteurs.getSelectedValuesList();
            List<Etiquette> etiquettes = listEtiquettes.getSelectedValuesList();

            if (reference == null) {
                Reference nouvelle = new Reference(
                        tfTitre.getText().trim(), tfRevue.getText().trim(), annee,
                        tfLien.getText().trim(), taDesc.getText().trim());
                controller.ajouterReference(nouvelle, auteurs, etiquettes);
            } else {
                reference.setTitre(tfTitre.getText().trim());
                reference.setRevue(tfRevue.getText().trim());
                reference.setAnnee(annee);
                reference.setHyperlien(tfLien.getText().trim());
                reference.setDescription(taDesc.getText().trim());
                controller.modifierReference(reference);
                controller.resynchroniserLiens(reference.getIdReference(), auteurs, etiquettes);
            }
            validated = true;
            dispose();
        } catch (Exception ex) {
            erreur(ex);
        }
    }

    private void erreur(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Erreur de validation", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isValidated() { return validated; }
}
