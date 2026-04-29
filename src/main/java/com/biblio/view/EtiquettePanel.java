package com.biblio.view;

import com.biblio.controller.EtiquetteController;
import com.biblio.model.Etiquette;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EtiquettePanel extends JPanel {
    private final EtiquetteController controller = new EtiquetteController();
    private final Model model = new Model();
    private final JTable table = new JTable(model);

    public EtiquettePanel() {
        setLayout(new BorderLayout(5,5));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btAj = new JButton("Ajouter");
        JButton btSup = new JButton("Supprimer");
        JButton btRe = new JButton("Rafraîchir");
        bottom.add(btAj); bottom.add(btSup); bottom.add(btRe);
        add(bottom, BorderLayout.SOUTH);

        btAj.addActionListener(e -> ajouter());
        btSup.addActionListener(e -> supprimer());
        btRe.addActionListener(e -> rafraichir());
        rafraichir();
    }

    private void ajouter() {
        String nom = JOptionPane.showInputDialog(this, "Nom de l'étiquette :");
        if (nom == null) return;
        try { controller.ajouter(new Etiquette(nom)); rafraichir(); }
        catch (Exception ex) { erreur(ex); }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        try { controller.supprimer(model.data.get(row).getIdEtiquette()); rafraichir(); }
        catch (SQLException ex) { erreur(ex); }
    }

    private void rafraichir() {
        try { model.setData(controller.lister()); }
        catch (SQLException e) { erreur(e); }
    }

    private void erreur(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private static class Model extends AbstractTableModel {
        private final String[] cols = {"ID", "Nom"};
        private List<Etiquette> data = java.util.Collections.emptyList();
        void setData(List<Etiquette> d) { this.data = d; fireTableDataChanged(); }
        public int getRowCount() { return data.size(); }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int c) { return cols[c]; }
        public Object getValueAt(int r, int c) {
            Etiquette e = data.get(r);
            return c == 0 ? e.getIdEtiquette() : e.getNom();
        }
    }
}
