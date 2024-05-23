package pl.edu.pg.eti.ksg.po.project2.rosliny;

import pl.edu.pg.eti.ksg.po.project2.Roslina;
import pl.edu.pg.eti.ksg.po.project2.Organizm;
import pl.edu.pg.eti.ksg.po.project2.Logs;
import pl.edu.pg.eti.ksg.po.project2.Swiat;
import pl.edu.pg.eti.ksg.po.project2.Punkt;
import pl.edu.pg.eti.ksg.po.project2.zwierzeta.Czlowiek;

import java.util.Random;
import java.awt.*;

public class WilczeJagody extends Roslina {
    private static final int SILA_WILCZE_JAGODY = 99;
    private static final int INICJATYWA_WILCZE_JAGODY = 0;

    public WilczeJagody(Swiat swiat, Punkt pozycja, int turaUrodzenia) {
        super(TypOrganizmu.WILCZE_JAGODY, swiat, pozycja, turaUrodzenia, SILA_WILCZE_JAGODY, INICJATYWA_WILCZE_JAGODY);
        setKolor(new Color(103, 6, 128));
        setSzansaRozmnazania(0.05);
    }


    @Override
    public void Akcja() {
        Random rand = new Random();
        int upperbound = 100;
        int tmpLosowanie = rand.nextInt(upperbound);
        if (tmpLosowanie < getSzansaRozmnazania() * 100) Rozprzestrzenianie();
    }

    @Override
    public String TypOrganizmuToString() {
        return "Wilcze jagody";
    }

    @Override
    public boolean ZdolnoscKolizji(Organizm atakujacy, Organizm ofiara) {
        if(atakujacy instanceof Czlowiek && ((Czlowiek) atakujacy ).getUmiejetnosc().getActive()){
            ((Czlowiek) atakujacy).Uciekaj(this);
            return true;
        }
        Logs.DodajKomentarz(atakujacy.OrganizmToString() + " zjada " + this.OrganizmToString());
        if (atakujacy.getSila() >= 99) {
            getSwiat().UsunOrganizm(this);
            Logs.DodajKomentarz(atakujacy.OrganizmToString() + " niszczy krzak wilczej jagody");
        }
        if (atakujacy.CzyJestZwierzeciem()) {
            getSwiat().UsunOrganizm(atakujacy);
            Logs.DodajKomentarz(atakujacy.OrganizmToString() + " ginie od wilczej jagody");
        }
        return true;
    }
}
