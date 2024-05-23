package pl.edu.pg.eti.ksg.po.project2;

import java.awt.*;
import java.util.Random;

public abstract class Organizm {
    public enum TypOrganizmu {
        CZLOWIEK,
        WILK,
        OWCA,
        LIS,
        ZOLW,
        ANTYLOPA,
        CYBER_OWCA,
        TRAWA,
        MLECZ,
        GUARANA,
        WILCZE_JAGODY,
        BARSZCZ_SOSNOWSKIEGO;
    }

    public enum Kierunek {
        LEWO(0),
        PRAWO(1),
        GORA(2),
        DOL(3),
        BRAK_KIERUNKU(4);

        private final int value;

        private Kierunek(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private int sila;
    private int inicjatywa;
    private int turaNarodzin;
    private Color kolor;
    private boolean isDead;
    private boolean[] kierunek;
    private boolean czySieRozmnazal;
    private Swiat swiat;
    private Punkt pozycja;
    private TypOrganizmu typOrganizmu;
    private double szansaRozmnazania;
    private static final int LICZBA_GATUNKOW = 12;

    public abstract String TypOrganizmuToString();

    public abstract void Akcja();

    public abstract void Kolizja(Organizm other);

    public abstract boolean CzyJestZwierzeciem();

    public Organizm(TypOrganizmu typOrganizmu, Swiat swiat,
                    Punkt pozycja, int turaNarodzin, int sila, int inicjatywa) {
        this.typOrganizmu = typOrganizmu;
        this.swiat = swiat;
        this.pozycja = pozycja;
        this.turaNarodzin = turaNarodzin;
        this.sila = sila;
        this.inicjatywa = inicjatywa;
        isDead = false;
        kierunek = new boolean[]{true, true, true, true};
    }

    public boolean ZdolnoscKolizji(Organizm atakujacy, Organizm ofiara) {
        return false;
    }

    public String OrganizmToString() {
        return (TypOrganizmuToString() + " x[" + pozycja.getX() + "] y["
                + pozycja.getY() + "] sila[" + sila + "]");
    }

    public void Ruch(Punkt przyszlaPozycja) {
        int x = przyszlaPozycja.getX();
        int y = przyszlaPozycja.getY();
        swiat.getPlansza()[pozycja.getY()][pozycja.getX()] = null;
        swiat.getPlansza()[y][x] = this;
        pozycja.setX(x);
        pozycja.setY(y);
    }

    static TypOrganizmu LosujTyp() {
        Random rand = new Random();
        int tmp = rand.nextInt(LICZBA_GATUNKOW - 1);
        if (tmp == 0) return TypOrganizmu.ANTYLOPA;
        if (tmp == 1) return TypOrganizmu.BARSZCZ_SOSNOWSKIEGO;
        if (tmp == 2) return TypOrganizmu.GUARANA;
        if (tmp == 3) return TypOrganizmu.LIS;
        if (tmp == 4) return TypOrganizmu.MLECZ;
        if (tmp == 5) return TypOrganizmu.OWCA;
        if (tmp == 6) return TypOrganizmu.TRAWA;
        if (tmp == 7) return TypOrganizmu.WILCZE_JAGODY;
        if (tmp == 8) return TypOrganizmu.WILK;
        if (tmp == 9) return TypOrganizmu.CYBER_OWCA;
        else return TypOrganizmu.ZOLW;
    }

    public Punkt LosowePole(Punkt pozycja) {
        AllowAllDirs();
        int pozX = pozycja.getX();
        int pozY = pozycja.getY();
        int sizeX = swiat.getSizeX();
        int sizeY = swiat.getSizeY();
        int ileKierunkowMozliwych = 0;

        if (pozX == 0) DeleteDirection(Kierunek.LEWO);
        else ileKierunkowMozliwych++;
        if (pozX == sizeX - 1) DeleteDirection(Kierunek.PRAWO);
        else ileKierunkowMozliwych++;
        if (pozY == 0) DeleteDirection(Kierunek.GORA);
        else ileKierunkowMozliwych++;
        if (pozY == sizeY - 1) DeleteDirection(Kierunek.DOL);
        else ileKierunkowMozliwych++;

        if (ileKierunkowMozliwych == 0) return pozycja;
        while (true) {
            Random rand = new Random();
            int upperbound = 100;
            int tmpLosowanie = rand.nextInt(upperbound);
            if (tmpLosowanie < 25 && !CzyKierunekZablokowany(Kierunek.LEWO))
                return new Punkt(pozX - 1, pozY);
            else if (tmpLosowanie >= 25 && tmpLosowanie < 50 && !CzyKierunekZablokowany(Kierunek.PRAWO))
                return new Punkt(pozX + 1, pozY);
            else if (tmpLosowanie >= 50 && tmpLosowanie < 75 && !CzyKierunekZablokowany(Kierunek.GORA))
                return new Punkt(pozX, pozY - 1);
            else if (tmpLosowanie >= 75 && !CzyKierunekZablokowany(Kierunek.DOL))
                return new Punkt(pozX, pozY + 1);
        }
    }
    public Punkt LosowePustePole(Punkt pozycja) {
        AllowAllDirs();
        int pozX = pozycja.getX();
        int pozY = pozycja.getY();
        int sizeX = swiat.getSizeX();
        int sizeY = swiat.getSizeY();
        int ileKierunkowMozliwych = 0;

        if (pozX == 0 || swiat.CzyPoleJestZajete(new Punkt(pozX - 1, pozY))) {
            DeleteDirection(Kierunek.LEWO);
        } else {
            ileKierunkowMozliwych++;
        }

        if (pozX == sizeX - 1 || swiat.CzyPoleJestZajete(new Punkt(pozX + 1, pozY))) {
            DeleteDirection(Kierunek.PRAWO);
        } else {
            ileKierunkowMozliwych++;
        }

        if (pozY == 0 || swiat.CzyPoleJestZajete(new Punkt(pozX, pozY - 1))) {
            DeleteDirection(Kierunek.GORA);
        } else {
            ileKierunkowMozliwych++;
        }

        if (pozY == sizeY - 1 || swiat.CzyPoleJestZajete(new Punkt(pozX, pozY + 1))) {
            DeleteDirection(Kierunek.DOL);
        } else {
            ileKierunkowMozliwych++;
        }

        if (ileKierunkowMozliwych == 0) {
            return new Punkt(pozX, pozY);
        }

        while (true) {
            int tmpLosowanie = new Random().nextInt(100);

            if (tmpLosowanie < 25 && !CzyKierunekZablokowany(Kierunek.LEWO)) {
                return new Punkt(pozX - 1, pozY);
            } else if (tmpLosowanie < 50 && !CzyKierunekZablokowany(Kierunek.PRAWO)) {
                return new Punkt(pozX + 1, pozY);
            } else if (tmpLosowanie < 75 && !CzyKierunekZablokowany(Kierunek.GORA)) {
                return new Punkt(pozX, pozY - 1);
            } else if (!CzyKierunekZablokowany(Kierunek.DOL)) {
                return new Punkt(pozX, pozY + 1);
            }
        }
    }

    protected void DeleteDirection(Kierunek kierunek) {
        this.kierunek[kierunek.getValue()] = false;
    }

    protected void AllowDirection(Kierunek kierunek) {
        this.kierunek[kierunek.getValue()] = true;
    }

    protected void AllowAllDirs() {
        AllowDirection(Kierunek.LEWO);
        AllowDirection(Kierunek.PRAWO);
        AllowDirection(Kierunek.GORA);
        AllowDirection(Kierunek.DOL);
    }

    protected boolean CzyKierunekZablokowany(Kierunek kierunek) {
        return !(this.kierunek[kierunek.getValue()]);
    }

    public int getSila() {
        return sila;
    }

    public int getInicjatywa() {
        return inicjatywa;
    }

    public int getTuraNarodzin() {
        return turaNarodzin;
    }

    public boolean getDead() {
        return isDead;
    }

    public boolean getCzySieRozmnazal() {
        return czySieRozmnazal;
    }

    public Swiat getSwiat() {
        return swiat;
    }

    public Punkt getPozycja() {
        return pozycja;
    }

    public TypOrganizmu getTypOrganizmu() {
        return typOrganizmu;
    }

    public void setSila(int sila) {
        this.sila = sila;
    }

    public void setInicjatywa(int inicjatywa) {
        this.inicjatywa = inicjatywa;
    }

    public void setTuraNarodzin(int turaNarodzin) {
        this.turaNarodzin = turaNarodzin;
    }

    public void setDead(boolean dead) {
        this.isDead = dead;
    }

    public void setCzySieRozmnazal(boolean czySieRozmnazal) {
        this.czySieRozmnazal = czySieRozmnazal;
    }

    public void setSwiat(Swiat swiat) {
        this.swiat = swiat;
    }

    public void setPozycja(Punkt pozycja) {
        this.pozycja = pozycja;
    }

    public void setTypOrganizmu(TypOrganizmu typOrganizmu) {
        this.typOrganizmu = typOrganizmu;
    }

    public Color getKolor() {
        return kolor;
    }

    public void setKolor(Color kolor) {
        this.kolor = kolor;
    }

    public double getSzansaRozmnazania() {
        return szansaRozmnazania;
    }

    public void setSzansaRozmnazania(double szansaRozmnazania) {
        this.szansaRozmnazania = szansaRozmnazania;
    }
}