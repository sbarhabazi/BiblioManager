package com.biblio.view;

import com.biblio.controller.AuteurController;
import com.biblio.model.Auteur;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AuteurPanel extends JPanel {
    private final AuteurController controller = new AuteurController();
    private final Model model = new Model();
    private final JTable table = new JTable(model);

    public AuteurPanel() {
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
        JTextField nom = new JTextField(), prenom = new JTextField();
        Object[] f = {"Nom :", nom, "Prénom :", prenom};
        if (JOptionPane.showConfirmDialog(this, f, "Nouvel auteur",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { controller.ajouter(new Auteur(nom.getText(), prenom.getText())); rafraichir(); }
        catch (Exception ex) { erreur(ex); }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        try { controller.supprimer(model.data.get(row).getIdAuteur()); rafraichir(); }
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
        private final String[] cols = {"ID", "Nom", "Prénom"};
        private List<Auteur> data = java.util.Collections.emptyList();
        void setData(List<Auteur> d) { this.data = d; fireTableDataChanged(); }
        public int getRowCount() { return data.size(); }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int c) { return cols[c]; }
        public Object getValueAt(int r, int c) {
            Auteur a = data.get(r);
            switch (c) { case 0: return a.getIdAuteur();
                case 1: return a.getNom(); default: return a.getPrenom(); }
        }
    }
}
