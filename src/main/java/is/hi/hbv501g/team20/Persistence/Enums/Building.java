package is.hi.hbv501g.team20.Persistence.Enums;

public enum Building {
    // all the building names that are included in our application
    ADAL("Aðalbygging"),
    ASKJA("Askja"),
    ARNAGARDUR("Árnagarður"),
    EDDA("Edda"),
    EIRBERG("Eirberg"),
    GIMLI("Gimli"),
    GROSKA("Gróska"),
    HASKOLABIO("Háskólabíó"),
    HASKOLATORG("Háskólatorg"),
    LAEKNAGARDUR("Læknagarður"),
    LOGBERG("Lögberg"),
    NYGARDUR("Nýgarður"),
    ODDI("Oddi"),
    SETBERG("Setberg"),
    SKIPHOLT("Skipholt"),
    STAKKAHLID("Stakkahlíð"),
    STAPI("Stapi"),
    TAEKNIGARDUR("Tæknigarður"),
    VEROLD("Veröld"),
    VR1("VR1"),
    VR2("VR2"),
    VR3("VR3");

    private final String name;

    Building(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    };

}
