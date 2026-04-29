package com.biblio.view;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale de l'application BiblioManager.
 * Organisation en onglets : Références, Auteurs, Étiquettes.
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("BiblioManager – Gestion de références bibliographiques");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Références", new ReferencePanel());
        tabs.addTab("Auteurs", new AuteurPanel());
        tabs.addTab("Étiquettes", new EtiquettePanel());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        add(new JLabel("  Projet No 2 – INF 4018 – Samuel Buhashe Barhabazi", SwingConstants.LEFT),
                BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignore) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
