package pl.edu.pg.eti.ksg.po.project2.rosliny;

import pl.edu.pg.eti.ksg.po.project2.Organizm;
import pl.edu.pg.eti.ksg.po.project2.Roslina;
import pl.edu.pg.eti.ksg.po.project2.Logs;
import pl.edu.pg.eti.ksg.po.project2.Swiat;
import pl.edu.pg.eti.ksg.po.project2.Punkt;
import pl.edu.pg.eti.ksg.po.project2.zwierzeta.Czlowiek;

import java.awt.*;
import java.util.Random;

public class BarszczSosnowskiego extends Roslina {
    private static final int SILA_BARSZCZ_SOSNOWSKIEGO = 10;
    private static final int INICJATYWA_BARSZCZ_SOSNOWSKIEGO = 0;

    public BarszczSosnowskiego(Swiat swiat, Punkt pozycja, int turaUrodzenia) {

        super(TypOrganizmu.BARSZCZ_SOSNOWSKIEGO, swiat, pozycja,
                turaUrodzenia, SILA_BARSZCZ_SOSNOWSKIEGO, INICJATYWA_BARSZCZ_SOSNOWSKIEGO);
        setKolor(new Color(204, 0, 204));
        setSzansaRozmnazania(0.05);
    }

    @Override
    public void Akcja() {
        int pozX = getPozycja().getX();
        int pozY = getPozycja().getY();
        LosowePole(getPozycja());// BLOKUJE GRANICE
        for (int i = 0; i < 4; i++) {
            Organizm tmpOrganizm = null;
            if (i == 0 && !CzyKierunekZablokowany(Kierunek.DOL))
                tmpOrganizm = getSwiat().getField(new Punkt(pozX, pozY + 1));
            else if (i == 1 && !CzyKierunekZablokowany(Kierunek.GORA))
                tmpOrganizm = getSwiat().getField(new Punkt(pozX, pozY - 1));
            else if (i == 2 && !CzyKierunekZablokowany(Kierunek.LEWO))
                tmpOrganizm = getSwiat().getField(new Punkt(pozX - 1, pozY));
            else if (i == 3 && !CzyKierunekZablokowany(Kierunek.PRAWO))
                tmpOrganizm = getSwiat().getField(new Punkt(pozX + 1, pozY));

            if (tmpOrganizm != null && tmpOrganizm.CzyJestZwierzeciem()
                    && tmpOrganizm.getTypOrganizmu() != TypOrganizmu.CYBER_OWCA) {
                if(tmpOrganizm instanceof Czlowiek && ((Czlowiek) tmpOrganizm).getUmiejetnosc().getActive()){
                    ((Czlowiek) tmpOrganizm).Uciekaj(this);
                }else{
                    getSwiat().UsunOrganizm(tmpOrganizm);
                    Logs.DodajKomentarz(OrganizmToString() + " zabija " + tmpOrganizm.OrganizmToString());
                }
            }
        }
        Random rand = new Random();
        int tmpLosowanie = rand.nextInt(100);
        if (tmpLosowanie < getSzansaRozmnazania() * 100) Rozprzestrzenianie();
    }

    @Override
    public String TypOrganizmuToString() {
        return "Barszcz Sosnowskiego";
    }

    @Override
    public boolean ZdolnoscKolizji(Organizm atakujacy, Organizm ofiara) {
        if (atakujacy.getSila() >= 10) {
            getSwiat().UsunOrganizm(this);
            Logs.DodajKomentarz(atakujacy.OrganizmToString() + " zjada " + this.OrganizmToString());
            atakujacy.Ruch(ofiara.getPozycja());
        }
        if ((atakujacy.CzyJestZwierzeciem() && atakujacy.getTypOrganizmu() != TypOrganizmu.CYBER_OWCA)
                || atakujacy.getSila() < 10) {
            getSwiat().UsunOrganizm(atakujacy);
            Logs.DodajKomentarz(this.OrganizmToString() + " zabija " + atakujacy.OrganizmToString());
        }
        return true;
    }
}
