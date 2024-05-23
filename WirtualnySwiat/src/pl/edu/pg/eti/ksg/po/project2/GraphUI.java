package pl.edu.pg.eti.ksg.po.project2;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GraphUI implements ActionListener, KeyListener {
    private Toolkit toolkit;
    private Dimension dimension;
    private JFrame jFrame;
    private JMenu menu;
    private JMenuItem newGame, load, save, exit;
    private Mapa mapa = null;
    private Logi logi = null;
    private Legenda legenda = null;
    private JPanel mainPanel;
    private final int ODSTEP;
    private Swiat swiat;

    public GraphUI(String title) {
        toolkit = Toolkit.getDefaultToolkit();
        dimension = toolkit.getScreenSize();
        ODSTEP = dimension.height / 100;


        jFrame = new JFrame(title);
        jFrame.setBounds((dimension.width - 800)/2, (dimension.height - 600) / 2, 1400, 900);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font font1 = new Font("SansSerif", Font.BOLD, 30);
        JMenuBar menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        newGame = new JMenuItem("New game");
        load = new JMenuItem("Load");
        save = new JMenuItem("Save");
        exit = new JMenuItem("Exit");
        menu.setFont(font1);
        newGame.setFont(font1);
        load.setFont(font1);
        save.setFont(font1);
        exit.setFont(font1);
        newGame.addActionListener(this);
        load.addActionListener(this);
        save.addActionListener(this);
        exit.addActionListener(this);
        menu.add(newGame);
        menu.add(load);
        menu.add(save);
        menu.add(exit);
        menuBar.add(menu);
        jFrame.setJMenuBar(menuBar);
        jFrame.setLayout(new CardLayout());

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.GRAY);
        mainPanel.setLayout(null);


        jFrame.addKeyListener(this);
        jFrame.add(mainPanel);
        jFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGame) {
            Logs.WyczyscKomentarzy();
            int sizeX = Integer.parseInt(JOptionPane.showInputDialog(jFrame,
                    "Podaj szerokosc swiata", "20"));
            int sizeY = Integer.parseInt(JOptionPane.showInputDialog(jFrame,
                    "Podaj wysokosc swiata", "20"));
            double zapelnienieSwiata = Double.parseDouble(JOptionPane.showInputDialog
                    (jFrame, "Podaj zapelnienie swiata(wartosc od 0 do 1)", "0.5"));
            swiat = new Swiat(sizeX, sizeY, this);
            swiat.CreateWorld(zapelnienieSwiata);
            if (mapa != null)
                mainPanel.remove(mapa);
            if (logi != null)
                mainPanel.remove(logi);
            if (legenda != null)
                mainPanel.remove(legenda);
            startGame();
        }
        if (e.getSource() == load) {
            Logs.WyczyscKomentarzy();
            String nameOfFile = JOptionPane.showInputDialog(jFrame, "Podaj nazwe pliku", "test");
            swiat = Swiat.OdtworzSwiat(nameOfFile);
            swiat.setSwiatGUI(this);
            mapa = new Mapa(swiat);
            logi = new Logi();
            legenda = new Legenda();
            if (mapa != null)
                mainPanel.remove(mapa);
            if (logi != null)
                mainPanel.remove(logi);
            if (legenda != null)
                mainPanel.remove(legenda);

            startGame();

        }
        if (e.getSource() == save) {
            String nameOfFile = JOptionPane.showInputDialog(jFrame, "Podaj nazwe pliku", "test");
            swiat.ZapiszSwiat(nameOfFile);
            Logs.DodajKomentarz("Swiat zostal zapisany");
            logi.odswiezKomentarze();
        }
        if (e.getSource() == exit) {
            jFrame.dispose();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (swiat != null && swiat.isPauza()) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER) {

            } else if (swiat.getCzyCzlowiekZyje()) {
                if (keyCode == KeyEvent.VK_UP) {
                    swiat.getCzlowiek().setKierunekRuchu(Organizm.Kierunek.GORA);
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    swiat.getCzlowiek().setKierunekRuchu(Organizm.Kierunek.DOL);
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    swiat.getCzlowiek().setKierunekRuchu(Organizm.Kierunek.LEWO);
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    swiat.getCzlowiek().setKierunekRuchu(Organizm.Kierunek.PRAWO);
                } else if (keyCode == KeyEvent.VK_P) {
                    SuperZdolnosc tmpSuperZdolnosc = swiat.getCzlowiek().getUmiejetnosc();
                    if (tmpSuperZdolnosc.getAvailable()) {
                        tmpSuperZdolnosc.Aktywuj();
                        Logs.DodajKomentarz("Umiejetnosc 'Niesmiertelnosc' zostala wlaczona (Pozostaly" +
                                " czas trwania wynosi " + tmpSuperZdolnosc.getCzasTrwania() + " tur)");

                    } else if (tmpSuperZdolnosc.getActive()) {
                        Logs.DodajKomentarz("Umiejetnosc juz zostala aktywowana " + "(Pozostaly" +
                                " czas trwania wynosi " + tmpSuperZdolnosc.getCzasTrwania() + " tur)");
                        logi.odswiezKomentarze();
                        return;
                    } else {
                        Logs.DodajKomentarz("Umiejetnosc mozna wlaczyc tylko po "
                                + tmpSuperZdolnosc.getCooldown() + " turach");
                        logi.odswiezKomentarze();
                        return;
                    }
                } else {
                    Logs.DodajKomentarz("\nNieoznaczony symbol, sprobuj ponownie");
                    logi.odswiezKomentarze();
                    return;
                }
            } else if (!swiat.getCzyCzlowiekZyje() && (keyCode == KeyEvent.VK_UP ||
                    keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT ||
                    keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_P)) {
                Logs.DodajKomentarz("Czlowiek umarl, nie mozesz im wiecej sterowac");
                logi.odswiezKomentarze();
                return;
            } else {
                Logs.DodajKomentarz("\nNieoznaczony symbol, sprobuj ponownie");
                logi.odswiezKomentarze();
                return;
            }
            Logs.WyczyscKomentarzy();
            swiat.setPauza(false);
            swiat.WykonajTure();
            odswiezSwiat();
            swiat.setPauza(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private class Mapa extends JPanel {
        private final int sizeX;
        private final int sizeY;
        private PolePlanszy[][] polaPlanszy;
        private Swiat SWIAT;

        public Mapa(Swiat swiat) {
            super();
            setBounds(mainPanel.getX() + ODSTEP, mainPanel.getY() + ODSTEP,
                    mainPanel.getWidth() * 6 / 8 - ODSTEP, mainPanel.getHeight() * 4 / 6 - ODSTEP);
            SWIAT = swiat;
            this.sizeX = swiat.getSizeX();
            this.sizeY = swiat.getSizeY();

            polaPlanszy = new PolePlanszy[sizeY][sizeX];
            for (int i = 0; i < sizeY; i++) {
                for (int j = 0; j < sizeX; j++) {
                    polaPlanszy[i][j] = new PolePlanszy(j, i);
                    polaPlanszy[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (e.getSource() instanceof PolePlanszy) {
                                PolePlanszy tmpPole = (PolePlanszy) e.getSource();
                                if (tmpPole.isEmpty == true) {
                                    ListaOrganizmow listaOrganizmow = new ListaOrganizmow
                                            (tmpPole.getX() + jFrame.getX(),
                                                    tmpPole.getY() + jFrame.getY(),
                                                    new Punkt(tmpPole.getPozX(), tmpPole.getPozY()));
                                }
                            }
                        }
                    });
                }
            }

            for (int i = 0; i < sizeY; i++) {
                for (int j = 0; j < sizeX; j++) {
                    this.add(polaPlanszy[i][j]);
                }
            }
            this.setLayout(new GridLayout(sizeY, sizeX));
        }

        private class PolePlanszy extends JButton {
            private boolean isEmpty;
            private Color kolor;
            private final int pozX;
            private final int pozY;

            public PolePlanszy(int X, int Y) {
                super();
                kolor = Color.WHITE;
                setBackground(kolor);
                isEmpty = true;
                pozX = X;
                pozY = Y;
            }

            public boolean isEmpty() {
                return isEmpty;
            }

            public void setEmpty(boolean empty) {
                isEmpty = empty;
            }


            public Color getKolor() {
                return kolor;
            }

            public void setKolor(Color kolor) {
                this.kolor = kolor;
                setBackground(kolor);
            }

            public int getPozX() {
                return pozX;
            }

            public int getPozY() {
                return pozY;
            }
        }

        public void odswiezPlansze() {
            for (int i = 0; i < sizeY; i++) {
                for (int j = 0; j < sizeX; j++) {
                    Organizm tmpOrganizm = swiat.getPlansza()[i][j];
                    if (tmpOrganizm != null) {
                        polaPlanszy[i][j].setEmpty(false);
                        polaPlanszy[i][j].setEnabled(false);
                        polaPlanszy[i][j].setKolor(tmpOrganizm.getKolor());
                    } else {
                        polaPlanszy[i][j].setEmpty(true);
                        polaPlanszy[i][j].setEnabled(true);
                        polaPlanszy[i][j].setKolor(Color.WHITE);
                    }
                }
            }
        }

        public int getSizeX() {
            return sizeX;
        }

        public int getSizeY() {
            return sizeY;
        }

        public PolePlanszy[][] getPolaPlanszy() {
            return polaPlanszy;
        }
    }

    private class Logi extends JPanel {
        private String tekst;
        private final String instriction = "Enter - nastepna tura\n" +
                "P - superzdolnosc\nStrzalki - ruch\n";
        private JTextArea textArea;

        public Logi() {
            super();
            Font font = new Font("Verdana", Font.BOLD, 14);
            setBounds(mainPanel.getX() + ODSTEP, mapa.getHeight()  + ODSTEP * 2,
                    mainPanel.getWidth() - ODSTEP * 2,
                    mainPanel.getHeight() * 2 / 7 - 2 * ODSTEP);
            tekst = Logs.getContent();
            textArea = new JTextArea(tekst);
            textArea.setFont(font);
            textArea.setForeground(Color.BLACK);
            textArea.setEditable(false);
            setLayout(new CardLayout());

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setMargin(new Insets(5, 5, 5, 5));
            JScrollPane sp = new JScrollPane(textArea);
            add(sp);
        }

        public void odswiezKomentarze() {
            tekst = instriction + Logs.getContent();
            textArea.setText(tekst);
        }
    }

    private class ListaOrganizmow extends JFrame {
        private String[] listaOrganizmow;
        private Organizm.TypOrganizmu[] typOrganizmuList;
        private JList jList;

        public ListaOrganizmow(int x, int y, Punkt punkt) {
            super("Lista organizmow");
            setBounds(x, y, 100, 300);
            listaOrganizmow = new String[]{"Barszcz Sosnowskiego", "Guarana", "Mlecz", "Trawa",
                    "Wilcze jagody", "Antylopa", "Lis", "Owca", "Wilk", "Zolw", "Cyber owca"};
            typOrganizmuList = new Organizm.TypOrganizmu[]{Organizm.TypOrganizmu.BARSZCZ_SOSNOWSKIEGO,
                    Organizm.TypOrganizmu.GUARANA, Organizm.TypOrganizmu.MLECZ, Organizm.TypOrganizmu.TRAWA,
                    Organizm.TypOrganizmu.WILCZE_JAGODY, Organizm.TypOrganizmu.ANTYLOPA,
                    Organizm.TypOrganizmu.LIS,
                    Organizm.TypOrganizmu.OWCA, Organizm.TypOrganizmu.WILK,
                    Organizm.TypOrganizmu.ZOLW, Organizm.TypOrganizmu.CYBER_OWCA
            };

            jList = new JList(listaOrganizmow);
            jList.setVisibleRowCount(listaOrganizmow.length);
            jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    Organizm tmpOrganizm = OrganismCreator.CreateNewOrganism
                            (typOrganizmuList[jList.getSelectedIndex()], swiat, punkt);
                    swiat.DodajOrganizm(tmpOrganizm);
                    Logs.DodajKomentarz("Stworzono nowy organizm " + tmpOrganizm.OrganizmToString());
                    odswiezSwiat();
                    dispose();

                }
            });

            JScrollPane sp = new JScrollPane(jList);
            add(sp);

            setVisible(true);
        }
    }

    private class Legenda extends JPanel {
        private final int ILOSC_TYPOW = 12;
        private JButton[] jButtons;

        public Legenda() {
            super();
            Font font1 = new Font("SansSerif", Font.BOLD, 16);
            setBounds(mapa.getX() + mapa.getWidth() + ODSTEP,
                    mainPanel.getY() + ODSTEP,
                    mainPanel.getWidth() - mapa.getWidth() - ODSTEP * 3,
                    mainPanel.getHeight() * 4/6 - ODSTEP);
            setBackground(Color.WHITE);
            setLayout(new FlowLayout(FlowLayout.CENTER));
            jButtons = new JButton[ILOSC_TYPOW];
            jButtons[0] = new JButton("<html>Barszcz<br/>Sosnowskiego</html>");
            jButtons[0].setFont(font1);
            jButtons[0].setBackground(new Color(204, 0, 204));
            jButtons[0].setPreferredSize(new Dimension(140, 70));


            jButtons[1] = new JButton("Guarana");
            jButtons[1].setFont(font1);
            jButtons[1].setBackground(new Color(220,20,60));
            jButtons[1].setPreferredSize(new Dimension(140, 70));


            jButtons[2] = new JButton("Mlecz");
            jButtons[2].setFont(font1);
            jButtons[2].setBackground(Color.YELLOW);
            jButtons[2].setPreferredSize(new Dimension(140, 70));


            jButtons[3] = new JButton("Trawa");
            jButtons[3].setFont(font1);
            jButtons[3].setBackground(Color.GREEN);
            jButtons[3].setPreferredSize(new Dimension(140, 70));

            jButtons[4] = new JButton("<html>Wilcze<br/>jagody</html>");
            jButtons[4].setFont(font1);
            jButtons[4].setBackground(new Color(103, 6, 128));
            jButtons[4].setPreferredSize(new Dimension(140, 70));


            jButtons[5] = new JButton("Antylopa");
            jButtons[5].setFont(font1);
            jButtons[5].setBackground(new Color(162, 124, 54));
            jButtons[5].setPreferredSize(new Dimension(140, 70));


            jButtons[6] = new JButton("Czlowiek");
            jButtons[6].setFont(font1);
            jButtons[6].setBackground(Color.BLUE);
            jButtons[6].setPreferredSize(new Dimension(140, 70));


            jButtons[7] = new JButton("Lis");
            jButtons[7].setFont(font1);
            jButtons[7].setBackground(new Color(255, 128, 0));
            jButtons[7].setPreferredSize(new Dimension(140, 70));


            jButtons[8] = new JButton("Owca");
            jButtons[8].setFont(font1);
            jButtons[8].setBackground(new Color(255, 153, 204));
            jButtons[8].setPreferredSize(new Dimension(140, 70));


            jButtons[9] = new JButton("Wilk");
            jButtons[9].setFont(font1);
            jButtons[9].setBackground(new Color(85, 139, 216));
            jButtons[9].setPreferredSize(new Dimension(140, 70));


            jButtons[10] = new JButton("Zolw");
            jButtons[10].setFont(font1);
            jButtons[10].setBackground(new Color(44, 254, 197));
            jButtons[10].setPreferredSize(new Dimension(140, 70));


            jButtons[11] = new JButton("<html>Cyber<br/>owca</html>");
            jButtons[11].setFont(font1);
            jButtons[11].setBackground(Color.black);
            jButtons[11].setPreferredSize(new Dimension(140, 70));


            for (int i = 0; i < ILOSC_TYPOW; i++) {
                jButtons[i].setEnabled(false);
                add(jButtons[i]);
            }

        }
    }

    private void startGame() {
        mapa = new Mapa(swiat);
        mainPanel.add(mapa);

        logi = new Logi();
        mainPanel.add(logi);

        legenda = new Legenda();
        mainPanel.add(legenda);

        odswiezSwiat();
    }

    public void odswiezSwiat() {
        mapa.odswiezPlansze();
        logi.odswiezKomentarze();
        SwingUtilities.updateComponentTreeUI(jFrame);
        jFrame.requestFocusInWindow();
    }

    public Swiat getSwiat() {
        return swiat;
    }

    public Mapa getPlanszaGraphics() {
        return mapa;
    }

    public Logi getKomentatorGraphics() {
        return logi;
    }
}