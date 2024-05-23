package pl.edu.pg.eti.ksg.po.project2.zwierzeta;

import pl.edu.pg.eti.ksg.po.project2.Zwierze;
import pl.edu.pg.eti.ksg.po.project2.Organizm;
import pl.edu.pg.eti.ksg.po.project2.Logs;
import pl.edu.pg.eti.ksg.po.project2.Swiat;
import pl.edu.pg.eti.ksg.po.project2.Punkt;
import pl.edu.pg.eti.ksg.po.project2.SuperZdolnosc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Czlowiek extends Zwierze {
    private static final int ZASIEG_RUCHU_CZLOWIEKA = 1;
    private static final int SZANSA_WYKONYWANIA_RUCHU_CZLOWIEKA = 1;
    private static final int SILA_CZLOWIEKA = 5;
    private static final int INICJATYWA_CZLOWIEKA = 4;
    private Kierunek kierunekRuchu;
    private SuperZdolnosc superZdolnosc;

    public Czlowiek(Swiat swiat, Punkt pozycja, int turaUrodzenia) {
        super(TypOrganizmu.CZLOWIEK, swiat, pozycja, turaUrodzenia, SILA_CZLOWIEKA, INICJATYWA_CZLOWIEKA);
        this.setZasiegRuchu(ZASIEG_RUCHU_CZLOWIEKA);
        this.setSzansaWykonywaniaRuchu(SZANSA_WYKONYWANIA_RUCHU_CZLOWIEKA);
        kierunekRuchu = Kierunek.BRAK_KIERUNKU;
        setKolor(Color.BLUE);
        superZdolnosc = new SuperZdolnosc();
    }

    public void Uciekaj(Organizm other){
        Organizm zaatakowany = getSwiat().getField(getPozycja());
        if(zaatakowany instanceof Czlowiek){
            getSwiat().clearField(getPozycja());
        }
        ArrayList<Punkt> punkty = new ArrayList<Punkt>();
        int x = getPozycja().getX();
        int y = getPozycja().getY();
        if(y-1 >= 0){
            Organizm org = getSwiat().getField(new Punkt(x,y-1));
            if(org == null || org.getSila() <= getSila()){
                punkty.add(new Punkt(x,y-1));
            }
        }
        if(x-1 >= 0){
            Organizm org = getSwiat().getField(new Punkt(x-1,y));
            if(org == null || org.getSila() <= getSila()){
                punkty.add(new Punkt(x-1,y));
            }
        }
        if(x + 1 < getSwiat().getSizeX()){
            Organizm org = getSwiat().getField(new Punkt(x+1,y));
            if(org == null || org.getSila() <= getSila()){
                punkty.add(new Punkt(x+1,y));
            }
        }
        if(y + 1 < getSwiat().getSizeY()){
            Organizm org = getSwiat().getField(new Punkt(x,y+1));
            if(org == null || org.getSila() <= getSila()){
                punkty.add(new Punkt(x,y+1));
            }
        }


        if(punkty.size() != 0){
            Random rand = new Random();
            int los = rand.nextInt(punkty.size());
            setPozycja(punkty.get(los));
            if(getSwiat().getField(getPozycja()) != null){
                getSwiat().UsunOrganizm(getSwiat().getField(getPozycja()));
                getSwiat().clearField(getPozycja());
            }
            getSwiat().setField(getPozycja(), this);
            Logs.DodajKomentarz("Czlowiek uciekl z pola.");
        }else {
            getSwiat().UsunOrganizm(this);
            Logs.DodajKomentarz(other.OrganizmToString() + " zabija " + OrganizmToString());
        }
        getSwiat().getSwiatGUI().odswiezSwiat();
    }

    @Override
    protected Punkt NowaPozycja() {
        int x = getPozycja().getX();
        int y = getPozycja().getY();
        LosowePole(getPozycja());//BLOKUJE KIERUNKI NIEDOZWOLONE PRZY GRANICY SWIATU
        if (kierunekRuchu == Kierunek.BRAK_KIERUNKU ||
                CzyKierunekZablokowany(kierunekRuchu)) return getPozycja();
        else {
            if (kierunekRuchu == Kierunek.DOL) return new Punkt(x, y + 1);
            if (kierunekRuchu == Kierunek.GORA) return new Punkt(x, y - 1);
            if (kierunekRuchu == Kierunek.LEWO) return new Punkt(x - 1, y);
            if (kierunekRuchu == Kierunek.PRAWO) return new Punkt(x + 1, y);
            else return new Punkt(x, y);
        }
    }

    @Override
    public void Akcja() {

        for (int i = 0; i < getZasiegRuchu(); i++) {
            Punkt przyszlaPozycja = NowaPozycja();

            if (getSwiat().CzyPoleJestZajete(przyszlaPozycja)) {
                Organizm organizmNaPolu = getSwiat().getField(przyszlaPozycja);
                if (organizmNaPolu != this) {
                    Kolizja(organizmNaPolu);
                    break;
                }
            } else {
                Ruch(przyszlaPozycja);
            }
        }

        kierunekRuchu = Kierunek.BRAK_KIERUNKU;
        superZdolnosc.Zmiana();
    }

    @Override
    public String TypOrganizmuToString() {
        return "Czlowiek";
    }

    public SuperZdolnosc getUmiejetnosc() {
        return superZdolnosc;
    }

    public void setKierunekRuchu(Kierunek kierunekRuchu) {
        this.kierunekRuchu = kierunekRuchu;
    }
}
