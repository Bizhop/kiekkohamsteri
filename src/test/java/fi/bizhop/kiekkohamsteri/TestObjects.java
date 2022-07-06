package fi.bizhop.kiekkohamsteri;

import fi.bizhop.kiekkohamsteri.model.*;
import fi.bizhop.kiekkohamsteri.projection.v1.*;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.*;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TestObjects {
    public static final String TEST_UUID = "d2b62756-378f-487d-ba25-0b0ff287d1d8";
    public static final String SHOULD_THROW_EXCEPTION = "Previous call should throw Exception";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String OTHER_EMAIL = "other@example.com";
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final Members TEST_USER = new Members(TEST_EMAIL);
    public static final Members OTHER_USER = new Members(OTHER_EMAIL);
    public static final Members ADMIN_USER = new Members(ADMIN_EMAIL);
    public static final List<R_valm> MANUFACTURERS;
    public static final List<R_mold> MOLDS;
    public static final List<R_muovi> PLASTICS;
    public static final List<R_vari> COLORS;
    public static final List<Dropdown> CONDITIONS;
    public static final List<Dropdown> MARKINGS;
    public static final List<Kiekot> DISCS;

    private static final Predicate<Kiekot> isNotLost = disc -> Boolean.FALSE.equals(disc.getLost());
    private static final Predicate<Kiekot> isLost = disc -> Boolean.TRUE.equals(disc.getLost());

    public static class Dropdown {
        public Integer id;
        public String name;

        public Dropdown(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static {
        TEST_USER.setUsername("User");
        OTHER_USER.setUsername("Other");
        ADMIN_USER.setUsername("Admin");
        ADMIN_USER.setLevel(2);

        var discmania = createManufacturer(0L, "Discmania");
        var innova = createManufacturer(1L, "Innova");
        var westside = createManufacturer(2L, "Westside");
        MANUFACTURERS = List.of(discmania, innova, westside);

        var fd = createMold(0L, discmania, "FD", 7D, 6D, -1D,1D);
        var pd = createMold(1L, discmania, "PD", 10D, 4D, 0D, 3D);
        var dessu = createMold(10L, innova, "Destroyer", 12D, 5D, -1D, 3D);
        var roc = createMold(11L, innova, "Roc", 4D, 4D, 0D, 2D);
        var harp = createMold(20L, westside, "Harp", 4D, 3D, 0D, 3D);
        var hatchet = createMold(21L, westside, "Hatchet", 9D, 6D, -2D, 2D);
        MOLDS = List.of(fd, pd, dessu, roc, harp, hatchet);

        var sLine = createPlastic(0L, discmania, "S-Line");
        var cLine = createPlastic(1L, discmania, "C-Line");
        var star = createPlastic(10L, innova, "Star");
        var champion = createPlastic(11L, innova, "Champion");
        var vip = createPlastic(20L, westside, "VIP");
        PLASTICS = List.of(sLine, cLine, star, champion, vip);

        var white = createColor(0L, "Valkoinen");
        var yellow = createColor(1L, "Keltainen");
        COLORS = List.of(white, yellow);

        var mint = new Dropdown(10, "10/10");
        var ok = new Dropdown(8, "8/10");
        var roller = new Dropdown(5, "5/10");
        var dogFood = new Dropdown(4, "4/10");
        CONDITIONS = List.of(mint, ok, roller, dogFood);

        var noMarkings = new Dropdown(0, "Ei merkintöjä");
        MARKINGS = List.of(noMarkings);

        var testUserDisc = new Kiekot(TEST_USER, fd, sLine, white);
        testUserDisc.setPublicDisc(true);
        var otherUserDisc = new Kiekot(OTHER_USER, harp, vip, yellow);
        otherUserDisc.setPublicDisc(true);
        var lostDisc = new Kiekot(TEST_USER, pd, cLine, yellow);
        lostDisc.setPublicDisc(true);
        lostDisc.setLost(true);
        var notPublicDisc = new Kiekot(TEST_USER, dessu, star, yellow);
        notPublicDisc.setPublicDisc(false);
        DISCS = List.of(testUserDisc, otherUserDisc, lostDisc, notPublicDisc);
    }

    public static List<DiscProjection> getDiscsByUser(Members user) {
        return DISCS.stream()
                .filter(isNotLost)
                .filter(disc -> disc.getMember().getEmail().equals(user.getEmail()))
                .map(TestObjects::projectionFromDisc)
                .collect(Collectors.toList());
    }

    public static List<DiscProjection> getLostDiscs() {
        return DISCS.stream()
                .filter(isLost)
                .map(TestObjects::projectionFromDisc)
                .collect(Collectors.toList());
    }

    public static List<ManufacturerDropdownProjection> getManufacturersDD() {
        return MANUFACTURERS.stream()
                .map(TestObjects::dropdownProjectionFromManufacturer)
                .collect(Collectors.toList());
    }

    public static List<MoldDropdownProjection> getMoldsDD() {
        return MOLDS.stream()
                .map(TestObjects::dropdownProjectionFromMold)
                .collect(Collectors.toList());
    }

    public static List<MoldDropdownProjection> getMoldsDD(R_valm manufacturer) {
        return MOLDS.stream()
                .filter(mold -> mold.getValmistaja().getId().equals(manufacturer.getId()))
                .map(TestObjects::dropdownProjectionFromMold)
                .collect(Collectors.toList());
    }

    public static List<MoldProjection> getMolds() {
        return MOLDS.stream()
                .map(TestObjects::projectionFromMold)
                .collect(Collectors.toList());
    }

    public static List<MoldProjection> getMolds(R_valm manufacturer) {
        return MOLDS.stream()
                .filter(mold -> mold.getValmistaja().getId().equals(manufacturer.getId()))
                .map(TestObjects::projectionFromMold)
                .collect(Collectors.toList());
    }

    public static List<PlasticDropdownProjection> getPlasticsDD() {
        return PLASTICS.stream()
                .map(TestObjects::dropdownProjectionFromPlastic)
                .collect(Collectors.toList());
    }

    public static List<PlasticDropdownProjection> getPlasticsDD(R_valm manufacturer) {
        return PLASTICS.stream()
                .filter(plastic -> plastic.getValmistaja().getId().equals(manufacturer.getId()))
                .map(TestObjects::dropdownProjectionFromPlastic)
                .collect(Collectors.toList());
    }

    public static List<ColorDropdownProjection> getColorsDD() {
        return COLORS.stream()
                .map(TestObjects::dropdownProjectionFromColor)
                .collect(Collectors.toList());
    }

    public static List<DropdownProjection> getConditionsDD() {
        return CONDITIONS.stream()
                .map(TestObjects::dropdownProjection)
                .collect(Collectors.toList());
    }

    public static List<DropdownProjection> getMarkingsDD() {
        return MARKINGS.stream()
                .map(TestObjects::dropdownProjection)
                .collect(Collectors.toList());
    }

    public static Kiekot getTestDiscFor(Members owner) {
        return new Kiekot(owner, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));
    }

    // HELPER METHODS

    private static R_vari createColor(Long id, String name) {
        var color = new R_vari();
        color.setId(id);
        color.setVari(name);
        return color;
    }

    private static R_muovi createPlastic(Long id, R_valm manufacturer, String name) {
        var plastic = new R_muovi();
        plastic.setId(id);
        plastic.setValmistaja(manufacturer);
        plastic.setMuovi(name);
        return plastic;
    }

    private static R_valm createManufacturer(Long id, String name) {
        var manufacturer = new R_valm();
        manufacturer.setId(id);
        manufacturer.setValmistaja(name);
        return manufacturer;
    }

    private static R_mold createMold(Long id, R_valm manufacturer, String disc, Double speed, Double glide, Double stability, Double fade) {
        var mold = new R_mold();
        mold.setId(id);
        mold.setValmistaja(manufacturer);
        mold.setKiekko(disc);
        mold.setNopeus(speed);
        mold.setLiito(glide);
        mold.setVakaus(stability);
        mold.setFeidi(fade);
        return mold;
    }


    // PROJECTIONS

    private static DropdownProjection dropdownProjection(Dropdown dropdown) {
        return new DropdownProjection() {
            @Override public Integer getId() { return dropdown.id; }
            @Override public String getNimi() { return dropdown.name; }
        };
    }

    private static ColorDropdownProjection dropdownProjectionFromColor(R_vari color) {
        return new ColorDropdownProjection() {
            @Override public Long getId() { return color.getId(); }
            @Override public String getVari() { return color.getVari(); }
        };
    }

    private static PlasticDropdownProjection dropdownProjectionFromPlastic(R_muovi plastic) {
        return new PlasticDropdownProjection() {
            @Override public Long getId() { return plastic.getId(); }
            @Override public String getMuovi() { return plastic.getMuovi();  }
        };
    }

    private static MoldDropdownProjection dropdownProjectionFromMold(R_mold mold) {
        return new MoldDropdownProjection() {
            @Override public Long getId() { return mold.getId(); }
            @Override public String getKiekko() { return mold.getKiekko(); }
        };
    }

    private static ManufacturerDropdownProjection dropdownProjectionFromManufacturer(R_valm manufacturer) {
        return new ManufacturerDropdownProjection() {
            @Override public Long getId() { return manufacturer.getId(); }
            @Override public String getValmistaja() { return manufacturer.getValmistaja(); }
        };
    }

    public static MoldProjection projectionFromMold(R_mold mold) {
        return new MoldProjection() {
            @Override public Long getId() { return mold.getId(); }
            @Override public String getValmistaja() { return mold.getValmistaja().getValmistaja(); }
            @Override public String getKiekko() { return mold.getKiekko(); }
            @Override public Double getNopeus() { return mold.getNopeus(); }
            @Override public Double getLiito() { return mold.getLiito(); }
            @Override public Double getVakaus() { return mold.getVakaus(); }
            @Override public Double getFeidi() { return mold.getFeidi(); }
        };
    }

    public static DiscProjection projectionFromDisc(Kiekot disc) {
        return new DiscProjection() {
            @Override public Long getId() { return disc.getId(); }
            @Override public String getOmistaja() { return disc.getMember().getUsername(); }
            @Override public String getOwnerEmail() { return disc.getMember().getEmail(); }
            @Override public String getValmistaja() { return disc.getMold().getValmistaja().getValmistaja(); }
            @Override public Long getValmId() { return disc.getMold().getValmistaja().getId(); }
            @Override public String getMold() { return disc.getMold().getKiekko(); }
            @Override public Long getMoldId() { return disc.getMold().getId(); }
            @Override public String getMuovi() { return disc.getMuovi().getMuovi(); }
            @Override public Long getMuoviId() { return disc.getMuovi().getId(); }
            @Override public String getVari() { return disc.getVari().getVari(); }
            @Override public Long getVariId() { return disc.getVari().getId(); }
            @Override public Double getNopeus() { return disc.getMold().getNopeus(); }
            @Override public Double getLiito() { return disc.getMold().getLiito(); }
            @Override public Double getVakaus() { return disc.getMold().getVakaus(); }
            @Override public Double getFeidi() { return disc.getMold().getFeidi(); }
            @Override public String getKuva() { return disc.getKuva(); }
            @Override public Integer getPaino() { return disc.getPaino(); }
            @Override public Integer getKunto() { return disc.getKunto(); }
            @Override public Boolean getHohto() { return disc.getHohto(); }
            @Override public Boolean getSpessu() { return disc.getSpessu(); }
            @Override public Boolean getDyed() { return disc.getDyed(); }
            @Override public Boolean getSwirly() { return disc.getSwirly(); }
            @Override public Integer getTussit() { return disc.getTussit(); }
            @Override public Boolean getMyynnissa() { return disc.getMyynnissa(); }
            @Override public Integer getHinta() { return disc.getHinta(); }
            @Override public String getMuuta() { return disc.getMuuta(); }
            @Override public Boolean getLoytokiekko() { return disc.getLoytokiekko(); }
            @Override public Boolean getItb() { return disc.getItb(); }
            @Override public Boolean getPublicDisc() { return disc.getPublicDisc(); }
            @Override public Boolean getLost() { return disc.getLost(); }
            @Override public Date getCreatedAt() { return disc.getCreatedAt(); }
            @Override public Date getUpdatedAt() { return disc.getUpdatedAt(); }
        };
    }
}
