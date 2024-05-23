package pl.edu.pg.eti.ksg.po.project2;

import pl.edu.pg.eti.ksg.po.project2.zwierzeta.Czlowiek;

import java.util.Random;

public abstract class Zwierze extends Organizm {
    private int zasiegRuchu;
    private double szansaWykonywaniaRuchu;

    public Zwierze(TypOrganizmu typOrganizmu, Swiat swiat,
                   Punkt pozycja, int turaUrodzenia, int sila, int inicjatywa) {
        super(typOrganizmu, swiat, pozycja, turaUrodzenia, sila, inicjatywa);
        setCzySieRozmnazal(false);
        setSzansaRozmnazania(0.5);
    }

    @Override
    public void Akcja() {
        for (int i = 0; i < zasiegRuchu; i++) {
            Punkt przyszlaPozycja = NowaPozycja();
            if (getSwiat().CzyPoleJestZajete(przyszlaPozycja)
                    && getSwiat().getField(przyszlaPozycja) != this) {
                Kolizja(getSwiat().getField(przyszlaPozycja));
                break;
            } else if (getSwiat().getField(przyszlaPozycja) != this){
                Ruch(przyszlaPozycja);
            }
        }
    }

    @Override
    public void Kolizja(Organizm other) {
        if (getTypOrganizmu() == other.getTypOrganizmu()) {
            if (new Random().nextInt(100) < getSzansaRozmnazania() * 100) {
                Rozmnazanie(other);
            }
        } else {
            if (other.ZdolnoscKolizji(this, other) || ZdolnoscKolizji(this, other)) {
                return;
            }

            if (getSila() >= other.getSila()) {
                if (other instanceof Czlowiek && ((Czlowiek) other).getUmiejetnosc().getActive()) {
                    ((Czlowiek) other).Uciekaj(this);
                } else {
                    getSwiat().UsunOrganizm(other);
                    Ruch(other.getPozycja());
                    Logs.DodajKomentarz(OrganizmToString() + " zabija " + other.OrganizmToString());
                }
            } else {
                if (this instanceof Czlowiek && ((Czlowiek) this).getUmiejetnosc().getActive()) {
                    ((Czlowiek) this).Uciekaj(other);
                } else {
                    getSwiat().UsunOrganizm(this);
                    Logs.DodajKomentarz(other.OrganizmToString() + " zabija " + OrganizmToString());
                }
            }
        }
    }


    @Override
    public boolean CzyJestZwierzeciem() {
        return true;
    }

    protected Punkt NowaPozycja() {
        int tmpLosowanie = new Random().nextInt(100);

        if (tmpLosowanie >= (int) (szansaWykonywaniaRuchu * 100)) {
            return getPozycja();
        } else {
            return LosowePole(getPozycja());
        }
    }

    private void Rozmnazanie(Organizm other) {
        if (this.getCzySieRozmnazal() || other.getCzySieRozmnazal()) {
            return;
        }

        Punkt tmpPunkt = this.LosowePustePole(getPozycja());
        if (tmpPunkt.equals(getPozycja())) {
            tmpPunkt = other.LosowePustePole(other.getPozycja());
            if (tmpPunkt.equals(other.getPozycja())) {
                return;
            }
        }

        Organizm tmpOrganizm = OrganismCreator.CreateNewOrganism(getTypOrganizmu(), this.getSwiat(), tmpPunkt);
        Logs.DodajKomentarz("Urodzil sie " + tmpOrganizm.OrganizmToString());
        getSwiat().DodajOrganizm(tmpOrganizm);
        setCzySieRozmnazal(true);
        other.setCzySieRozmnazal(true);
    }

    public int getZasiegRuchu() {
        return zasiegRuchu;
    }

    public void setZasiegRuchu(int zasiegRuchu) {
        this.zasiegRuchu = zasiegRuchu;
    }

    public double getSzansaWykonywaniaRuchu() {
        return szansaWykonywaniaRuchu;
    }

    public void setSzansaWykonywaniaRuchu(double szansaWykonywaniaRuchu) {
        this.szansaWykonywaniaRuchu = szansaWykonywaniaRuchu;
    }
}
