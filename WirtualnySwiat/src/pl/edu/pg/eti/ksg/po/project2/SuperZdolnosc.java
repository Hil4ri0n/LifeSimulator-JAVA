package pl.edu.pg.eti.ksg.po.project2;

public class SuperZdolnosc {
    private final int CZAS_TRWANIA_ZDOLNOSCI = 5;
    private final int COOLDOWNZDOLNOSCI = 10;
    private boolean isAvailable;
    private boolean isActive;
    private int czasTrwania;
    private int cooldown;

    public SuperZdolnosc() {
        cooldown = 0;
        czasTrwania = 0;
        isActive = false;
        isAvailable = true;
    }

    public void Zmiana() {
        if (cooldown > 0) cooldown--;
        if (czasTrwania > 0) czasTrwania--;
        if (czasTrwania == 0) Dezaktywuj();
        if (cooldown == 0) isAvailable = true;
    }

    public void Aktywuj() {
        if (cooldown == 0) {
            isActive = true;
            isAvailable = false;
            cooldown = COOLDOWNZDOLNOSCI;
            czasTrwania = CZAS_TRWANIA_ZDOLNOSCI;
        }
    }

    public void Dezaktywuj() {
        isActive = false;
    }

    public boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public int getCzasTrwania() {
        return czasTrwania;
    }

    public void setCzasTrwania(int czasTrwania) {
        this.czasTrwania = czasTrwania;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
