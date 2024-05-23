package pl.edu.pg.eti.ksg.po.project2;

import java.io.IOException;
import java.util.*;
import java.io.File;
import java.io.PrintWriter;

import pl.edu.pg.eti.ksg.po.project2.zwierzeta.Czlowiek;

public class Swiat {
    private int sizeX;
    private int sizeY;
    private int numerTury;
    private Organizm[][] plansza;
    private boolean czyCzlowiekZyje;
    private boolean czyJestKoniecGry;
    private boolean pauza;
    private ArrayList<Organizm> organizmy;
    private Czlowiek czlowiek;
    private GraphUI graphUI;

    public Swiat(GraphUI graphUI) {
        organizmy = new ArrayList<>();
        this.graphUI = graphUI;
        sizeX = 0;
        sizeY = 0;
        czyCzlowiekZyje = true;
        czyJestKoniecGry = false;
        pauza = true;
        numerTury = 0;
    }

    public Swiat(int sizeX, int sizeY, GraphUI graphUI) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        numerTury = 0;
        czyCzlowiekZyje = true;
        czyJestKoniecGry = false;
        pauza = true;
        plansza = new Organizm[sizeY][sizeX];
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                plansza[i][j] = null;
            }
        }
        organizmy = new ArrayList<>();
        this.graphUI = graphUI;
    }


    public void ZapiszSwiat(String nameOfFile) {
        try {
            nameOfFile += ".txt";
            File file = new File(nameOfFile);
            file.createNewFile();

            PrintWriter pw = new PrintWriter(file);
            pw.print(sizeX + " ");
            pw.print(sizeY + " ");
            pw.print(numerTury + " ");
            pw.print(czyCzlowiekZyje + " ");
            pw.print(czyJestKoniecGry + "\n");
            for (int i = 0; i < organizmy.size(); i++) {
                pw.print(organizmy.get(i).getTypOrganizmu() + " ");
                pw.print(organizmy.get(i).getPozycja().getX() + " ");
                pw.print(organizmy.get(i).getPozycja().getY() + " ");
                pw.print(organizmy.get(i).getSila() + " ");
                pw.print(organizmy.get(i).getTuraNarodzin() + " ");
                pw.print(organizmy.get(i).getDead());
                if (organizmy.get(i).getTypOrganizmu() == Organizm.TypOrganizmu.CZLOWIEK) {
                    pw.print(" " + czlowiek.getUmiejetnosc().getCzasTrwania() + " ");
                    pw.print(czlowiek.getUmiejetnosc().getCooldown() + " ");
                    pw.print(czlowiek.getUmiejetnosc().getActive() + " ");
                    pw.print(czlowiek.getUmiejetnosc().getAvailable());
                }
                pw.println();
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public static Swiat OdtworzSwiat(String nameOfFile) {
        try {
            nameOfFile += ".txt";
            File file = new File(nameOfFile);

            Scanner scanner = new Scanner(file);
            String line = scanner.nextLine();
            String[] properties = line.split(" ");
            int sizeX = Integer.parseInt(properties[0]);
            int sizeY = Integer.parseInt(properties[1]);
            Swiat tmpSwiat = new Swiat(sizeX, sizeY, null);
            int numerTury = Integer.parseInt(properties[2]);
            tmpSwiat.numerTury = numerTury;
            boolean czyCzlowiekZyje = Boolean.parseBoolean(properties[3]);
            tmpSwiat.czyCzlowiekZyje = czyCzlowiekZyje;
            boolean czyJestKoniecGry = Boolean.parseBoolean(properties[4]);
            tmpSwiat.czyJestKoniecGry = czyJestKoniecGry;
            tmpSwiat.czlowiek = null;

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                properties = line.split(" ");
                Organizm.TypOrganizmu typOrganizmu = Organizm.TypOrganizmu.valueOf(properties[0]);
                int x = Integer.parseInt(properties[1]);
                int y = Integer.parseInt(properties[2]);

                Organizm tmpOrganizm = OrganismCreator.CreateNewOrganism
                        (typOrganizmu, tmpSwiat, new Punkt(x, y));
                int sila = Integer.parseInt(properties[3]);
                tmpOrganizm.setSila(sila);
                int turaUrodzenia = Integer.parseInt(properties[4]);
                tmpOrganizm.setTuraNarodzin(turaUrodzenia);
                boolean czyUmarl = Boolean.parseBoolean(properties[5]);
                tmpOrganizm.setDead(czyUmarl);

                if (typOrganizmu == Organizm.TypOrganizmu.CZLOWIEK) {
                    tmpSwiat.czlowiek = (Czlowiek) tmpOrganizm;
                    int czasTrwania = Integer.parseInt(properties[6]);
                    tmpSwiat.czlowiek.getUmiejetnosc().setCzasTrwania(czasTrwania);
                    int cooldown = Integer.parseInt(properties[7]);
                    tmpSwiat.czlowiek.getUmiejetnosc().setCooldown(cooldown);
                    boolean czyJestAktywna = Boolean.parseBoolean(properties[8]);
                    tmpSwiat.czlowiek.getUmiejetnosc().setActive(czyJestAktywna);
                    boolean czyMoznaAktywowac = Boolean.parseBoolean(properties[9]);
                    tmpSwiat.czlowiek.getUmiejetnosc().setAvailable(czyMoznaAktywowac);
                }
                tmpSwiat.DodajOrganizm(tmpOrganizm);
            }
            scanner.close();
            return tmpSwiat;
        } catch (
                IOException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public void CreateWorld(double zapelnienieSwiata) {
        int liczbaOrganizmow = (int) Math.floor(sizeX * sizeY * zapelnienieSwiata);

        Punkt pozycja = LosoweWolnePole();
        Organizm tmpOrganizm = OrganismCreator.CreateNewOrganism(Organizm.TypOrganizmu.CZLOWIEK, this, pozycja);
        DodajOrganizm(tmpOrganizm);
        czlowiek = (Czlowiek) tmpOrganizm;

        for (int i = 0; i < liczbaOrganizmow - 1; i++) {
            pozycja = LosoweWolnePole();
            if (pozycja == new Punkt(-1, -1)) {
                return;
            }
            DodajOrganizm(OrganismCreator.CreateNewOrganism(Organizm.LosujTyp(), this, pozycja));
        }
    }



    public void WykonajTure() {
        if (czyJestKoniecGry) return;
        numerTury++;
        Logs.DodajKomentarz("\nTURA " + numerTury);
        System.out.println(numerTury);
        System.out.println(organizmy.size() + "\n");
        SortujOrganizmy();
        for (int i = 0; i < organizmy.size(); i++) {
            if (organizmy.get(i).getTuraNarodzin() != numerTury
                    && organizmy.get(i).getDead() == false) {
                organizmy.get(i).Akcja();
            }
        }
        for (int i = 0; i < organizmy.size(); i++) {
            if (organizmy.get(i).getDead() == true) {
                organizmy.remove(i);
                i--;
            }
        }
        for (int i = 0; i < organizmy.size(); i++) {
            organizmy.get(i).setCzySieRozmnazal(false);
        }
    }

    private void SortujOrganizmy() {
        Collections.sort(organizmy, new Comparator<Organizm>() {
            @Override
            public int compare(Organizm o1, Organizm o2) {
                if (o1.getInicjatywa() != o2.getInicjatywa())
                    return Integer.valueOf(o2.getInicjatywa()).compareTo(o1.getInicjatywa());
                else
                    return Integer.valueOf(o1.getTuraNarodzin()).compareTo(o2.getTuraNarodzin());
            }
        });
    }

    public Punkt LosoweWolnePole() {
        Random rand = new Random();
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (plansza[i][j] == null) {
                    while (true) {
                        int x = rand.nextInt(sizeX);
                        int y = rand.nextInt(sizeY);
                        if (plansza[y][x] == null) return new Punkt(x, y);
                    }
                }
            }
        }
        return new Punkt(-1, -1);
    }

    public boolean CzyPoleJestZajete(Punkt pole) {
        if (plansza[pole.getY()][pole.getX()] == null) return false;
        else return true;
    }

    public Organizm getField(Punkt pole) {
        return plansza[pole.getY()][pole.getX()];
    }

    public void clearField(Punkt pole){
        plansza[pole.getY()][pole.getX()] = null;
    }

    public void setField(Punkt pole, Organizm organizm){
        plansza[pole.getY()][pole.getX()] = organizm;
    }

    public void DodajOrganizm(Organizm organizm) {
        organizmy.add(organizm);
        plansza[organizm.getPozycja().getY()][organizm.getPozycja().getX()] = organizm;
    }

    public void UsunOrganizm(Organizm organizm) {
        plansza[organizm.getPozycja().getY()][organizm.getPozycja().getX()] = null;
        organizm.setDead(true);
        if (organizm.getTypOrganizmu() == Organizm.TypOrganizmu.CZLOWIEK) {
            czyCzlowiekZyje = false;
            czlowiek = null;
        }
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getNumerTury() {
        return numerTury;
    }

    public Organizm[][] getPlansza() {
        return plansza;
    }

    public boolean getCzyCzlowiekZyje() {
        return czyCzlowiekZyje;
    }

    public boolean getCzyJestKoniecGry() {
        return czyJestKoniecGry;
    }

    public ArrayList<Organizm> getOrganizmy() {
        return organizmy;
    }

    public Czlowiek getCzlowiek() {
        return czlowiek;
    }

    public void setCzlowiek(Czlowiek czlowiek) {
        this.czlowiek = czlowiek;
    }

    public void setCzyCzlowiekZyje(boolean czyCzlowiekZyje) {
        this.czyCzlowiekZyje = czyCzlowiekZyje;
    }

    public void setCzyJestKoniecGry(boolean czyJestKoniecGry) {
        this.czyJestKoniecGry = czyJestKoniecGry;
    }

    public boolean isPauza() {
        return pauza;
    }

    public void setPauza(boolean pauza) {
        this.pauza = pauza;
    }

    public GraphUI getSwiatGUI() {
        return graphUI;
    }

    public void setSwiatGUI(GraphUI graphUI) {
        this.graphUI = graphUI;
    }

    public boolean czyIstniejeBarszczSosnowskiego() {
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (plansza[i][j] != null &&
                        plansza[i][j].getTypOrganizmu() == Organizm.TypOrganizmu.BARSZCZ_SOSNOWSKIEGO) {
                    return true;
                }
            }
        }
        return false;
    }
}
