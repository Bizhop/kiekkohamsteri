package fi.bizhop.kiekkohamsteri;

import fi.bizhop.kiekkohamsteri.model.*;
import fi.bizhop.kiekkohamsteri.projection.v1.*;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.*;
import fi.bizhop.kiekkohamsteri.util.Utils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_ADMIN;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_GROUP_ADMIN;

public class TestObjects {
    public static final String TEST_UUID = "d2b62756-378f-487d-ba25-0b0ff287d1d8";
    public static final String SHOULD_THROW_EXCEPTION = "Previous call should throw Exception";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String OTHER_EMAIL = "other@example.com";
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String GROUP_ADMIN_EMAIL = "group-admin@example.com";
    public static final User TEST_USER = new User(TEST_EMAIL);
    public static final User OTHER_USER = new User(OTHER_EMAIL);
    public static final User ADMIN_USER = new User(ADMIN_EMAIL);
    public static final User GROUP_ADMIN_USER = new User(GROUP_ADMIN_EMAIL);
    public static final List<Manufacturer> MANUFACTURERS;
    public static final List<Mold> MOLDS;
    public static final List<Plastic> PLASTICS;
    public static final List<Color> COLORS;
    public static final List<Dropdown> CONDITIONS;
    public static final List<Dropdown> MARKINGS;
    public static final List<Disc> DISCS;
    public static final List<User> USERS;
    public static final List<User> GROUP_USERS;
    public static final List<Group> GROUPS;

    private static final Predicate<Disc> isNotLost = disc -> Boolean.FALSE.equals(disc.getLost());
    private static final Predicate<Disc> isLost = disc -> Boolean.TRUE.equals(disc.getLost());

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
        GROUP_ADMIN_USER.setUsername("Group admin");

        ADMIN_USER.setLevel(2);
        ADMIN_USER.setRoles(Set.of(new Role(1L, USER_ROLE_ADMIN, null)));

        GROUP_ADMIN_USER.setRoles(Set.of(new Role(2L, USER_ROLE_GROUP_ADMIN, 1L)));

        USERS = List.of(TEST_USER, OTHER_USER, ADMIN_USER, GROUP_ADMIN_USER);
        GROUP_USERS = List.of(TEST_USER, GROUP_ADMIN_USER);

        GROUPS = List.of(new Group(1L, "group 1"), new Group(2L, "group 2"));

        TEST_USER.getGroups().add(GROUPS.get(0));
        GROUP_ADMIN_USER.getGroups().add(GROUPS.get(0));

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

        var testUserDisc = new Disc(TEST_USER, fd, sLine, white);
        testUserDisc.setPublicDisc(true);
        var otherUserDisc = new Disc(OTHER_USER, harp, vip, yellow);
        otherUserDisc.setPublicDisc(true);
        var lostDisc = new Disc(TEST_USER, pd, cLine, yellow);
        lostDisc.setPublicDisc(true);
        lostDisc.setLost(true);
        var notPublicDisc = new Disc(TEST_USER, dessu, star, yellow);
        notPublicDisc.setPublicDisc(false);
        DISCS = List.of(testUserDisc, otherUserDisc, lostDisc, notPublicDisc);
    }

    public static List<DiscProjection> getDiscsByUser(User user) {
        return DISCS.stream()
                .filter(isNotLost)
                .filter(disc -> disc.getOwner().getEmail().equals(user.getEmail()))
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

    public static List<MoldDropdownProjection> getMoldsDD(Manufacturer manufacturer) {
        return MOLDS.stream()
                .filter(mold -> mold.getManufacturer().getId().equals(manufacturer.getId()))
                .map(TestObjects::dropdownProjectionFromMold)
                .collect(Collectors.toList());
    }

    public static List<MoldProjection> getMolds() {
        return MOLDS.stream()
                .map(TestObjects::projectionFromMold)
                .collect(Collectors.toList());
    }

    public static List<MoldProjection> getMolds(Manufacturer manufacturer) {
        return MOLDS.stream()
                .filter(mold -> mold.getManufacturer().getId().equals(manufacturer.getId()))
                .map(TestObjects::projectionFromMold)
                .collect(Collectors.toList());
    }

    public static List<PlasticProjection> getPlastics() {
        return PLASTICS.stream()
                .map(TestObjects::projectionFromPlastic)
                .collect(Collectors.toList());
    }

    public static List<PlasticProjection> getPlastics(Manufacturer manufacturer) {
        return PLASTICS.stream()
                .filter(mold -> mold.getManufacturer().getId().equals(manufacturer.getId()))
                .map(TestObjects::projectionFromPlastic)
                .collect(Collectors.toList());
    }

    public static List<PlasticDropdownProjection> getPlasticsDD() {
        return PLASTICS.stream()
                .map(TestObjects::dropdownProjectionFromPlastic)
                .collect(Collectors.toList());
    }

    public static List<PlasticDropdownProjection> getPlasticsDD(Manufacturer manufacturer) {
        return PLASTICS.stream()
                .filter(plastic -> plastic.getManufacturer().getId().equals(manufacturer.getId()))
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

    public static Disc getTestDiscFor(User owner) {
        return new Disc(owner, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));
    }

    // HELPER METHODS

    private static Color createColor(Long id, String name) {
        var color = new Color();
        color.setId(id);
        color.setName(name);
        return color;
    }

    private static Plastic createPlastic(Long id, Manufacturer manufacturer, String name) {
        var plastic = new Plastic();
        plastic.setId(id);
        plastic.setManufacturer(manufacturer);
        plastic.setName(name);
        return plastic;
    }

    private static Manufacturer createManufacturer(Long id, String name) {
        var manufacturer = new Manufacturer();
        manufacturer.setId(id);
        manufacturer.setName(name);
        return manufacturer;
    }

    private static Mold createMold(Long id, Manufacturer manufacturer, String disc, Double speed, Double glide, Double stability, Double fade) {
        var mold = new Mold();
        mold.setId(id);
        mold.setManufacturer(manufacturer);
        mold.setName(disc);
        mold.setSpeed(speed);
        mold.setGlide(glide);
        mold.setStability(stability);
        mold.setFade(fade);
        return mold;
    }


    // PROJECTIONS

    private static DropdownProjection dropdownProjection(Dropdown dropdown) {
        return new DropdownProjection() {
            @Override public Integer getId() { return dropdown.id; }
            @Override public String getNimi() { return dropdown.name; }
        };
    }

    private static ColorDropdownProjection dropdownProjectionFromColor(Color color) {
        return new ColorDropdownProjection() {
            @Override public Long getId() { return color.getId(); }
            @Override public String getVari() { return color.getName(); }
        };
    }

    private static PlasticDropdownProjection dropdownProjectionFromPlastic(Plastic plastic) {
        return new PlasticDropdownProjection() {
            @Override public Long getId() { return plastic.getId(); }
            @Override public String getMuovi() { return plastic.getName();  }
        };
    }

    private static MoldDropdownProjection dropdownProjectionFromMold(Mold mold) {
        return new MoldDropdownProjection() {
            @Override public Long getId() { return mold.getId(); }
            @Override public String getKiekko() { return mold.getName(); }
        };
    }

    private static ManufacturerDropdownProjection dropdownProjectionFromManufacturer(Manufacturer manufacturer) {
        return new ManufacturerDropdownProjection() {
            @Override public Long getId() { return manufacturer.getId(); }
            @Override public String getValmistaja() { return manufacturer.getName(); }
        };
    }

    public static MoldProjection projectionFromMold(Mold mold) {
        return new MoldProjection() {
            @Override public Long getId() { return mold.getId(); }
            @Override public String getValmistaja() { return mold.getManufacturer().getName(); }
            @Override public String getKiekko() { return mold.getName(); }
            @Override public Double getNopeus() { return mold.getSpeed(); }
            @Override public Double getLiito() { return mold.getGlide(); }
            @Override public Double getVakaus() { return mold.getStability(); }
            @Override public Double getFeidi() { return mold.getFade(); }
        };
    }

    public static PlasticProjection projectionFromPlastic(Plastic plastic) {
        return new PlasticProjection() {
            @Override public Long getId() { return plastic.getId(); }
            @Override public String getValmistaja() { return plastic.getManufacturer().getName(); }
            @Override public String getMuovi() { return plastic.getName(); }
        };
    }

    public static DiscProjection projectionFromDisc(Disc disc) {
        return new DiscProjection() {
            @Override public Long getId() { return disc.getId(); }
            @Override public String getOmistaja() { return disc.getOwner().getUsername(); }
            @Override public String getOwnerEmail() { return disc.getOwner().getEmail(); }
            @Override public String getValmistaja() { return disc.getMold().getManufacturer().getName(); }
            @Override public Long getValmId() { return disc.getMold().getManufacturer().getId(); }
            @Override public String getMold() { return disc.getMold().getName(); }
            @Override public Long getMoldId() { return disc.getMold().getId(); }
            @Override public String getMuovi() { return disc.getPlastic().getName(); }
            @Override public Long getMuoviId() { return disc.getPlastic().getId(); }
            @Override public String getVari() { return disc.getColor().getName(); }
            @Override public Long getVariId() { return disc.getColor().getId(); }
            @Override public Double getNopeus() { return disc.getMold().getSpeed(); }
            @Override public Double getLiito() { return disc.getMold().getGlide(); }
            @Override public Double getVakaus() { return disc.getMold().getStability(); }
            @Override public Double getFeidi() { return disc.getMold().getFade(); }
            @Override public String getKuva() { return disc.getImage(); }
            @Override public Integer getPaino() { return disc.getWeight(); }
            @Override public Integer getKunto() { return disc.getCondition(); }
            @Override public Boolean getHohto() { return disc.getGlow(); }
            @Override public Boolean getSpessu() { return disc.getSpecial(); }
            @Override public Boolean getDyed() { return disc.getDyed(); }
            @Override public Boolean getSwirly() { return disc.getSwirly(); }
            @Override public Integer getTussit() { return disc.getMarkings(); }
            @Override public Boolean getMyynnissa() { return disc.getForSale(); }
            @Override public Integer getHinta() { return disc.getPrice(); }
            @Override public String getMuuta() { return disc.getDescription(); }
            @Override public Boolean getLoytokiekko() { return disc.getLostAndFound(); }
            @Override public Boolean getItb() { return disc.getItb(); }
            @Override public Boolean getPublicDisc() { return disc.getPublicDisc(); }
            @Override public Boolean getLost() { return disc.getLost(); }
            @Override public Date getCreatedAt() { return disc.getCreatedAt(); }
            @Override public Date getUpdatedAt() { return disc.getUpdatedAt(); }
        };
    }
}
