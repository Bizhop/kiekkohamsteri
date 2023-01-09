package fi.bizhop.kiekkohamsteri;

import fi.bizhop.kiekkohamsteri.dto.v2.out.DropdownOutputDto;
import fi.bizhop.kiekkohamsteri.model.*;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_ADMIN;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_GROUP_ADMIN;

public class TestObjects {
    public static final Instant TEST_TIMESTAMP = Instant.ofEpochMilli(1670917528851L);
    public static final String SHOULD_THROW_EXCEPTION = "Previous call should throw Exception";
    public static final String WRONG_EXCEPTION = "Expected different exception";
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
    public static final List<DropdownValues> CONDITIONS;
    public static final List<DropdownValues> MARKINGS;
    public static final List<Disc> DISCS;
    public static final List<User> USERS;
    public static final List<User> GROUP_USERS;
    public static final List<Group> GROUPS;

    private static final Predicate<Disc> isNotLost = disc -> Boolean.FALSE.equals(disc.getLost());
    private static final Predicate<Disc> isLost = disc -> Boolean.TRUE.equals(disc.getLost());

    static {
        TEST_USER.setUsername("User");
        OTHER_USER.setUsername("Other");
        ADMIN_USER.setUsername("Admin");
        GROUP_ADMIN_USER.setUsername("Group admin");

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

        var mint = new DropdownValues(null, "kunto", "10/10", 10L);
        var ok = new DropdownValues(null, "kunto","8/10", 8L);
        var roller = new DropdownValues(null, "kunto", "5/10", 5L);
        var dogFood = new DropdownValues(null, "kunto", "4/10", 4L);
        CONDITIONS = List.of(mint, ok, roller, dogFood);

        var noMarkings = new DropdownValues(null, "tussit", "Ei merkintöjä", 0L);
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

    public static List<Disc> getDiscsByUserV2(User user) {
        return DISCS.stream()
                .filter(isNotLost)
                .filter(disc -> disc.getOwner().getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getManufacturersDD() {
        return MANUFACTURERS.stream()
                .map(DropdownOutputDto::fromDropdownInterface)
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getMoldsDD() {
        return MOLDS.stream()
                .map(DropdownOutputDto::fromDropdownInterface)
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getMoldsDD(Manufacturer manufacturer) {
        return MOLDS.stream()
                .filter(mold -> mold.getManufacturer().getId().equals(manufacturer.getId()))
                .map(DropdownOutputDto::fromDropdownInterface)
                .collect(Collectors.toList());
    }

    public static List<Mold> getMolds(Manufacturer manufacturer) {
        return MOLDS.stream()
                .filter(mold -> mold.getManufacturer().getId().equals(manufacturer.getId()))
                .collect(Collectors.toList());
    }

    public static List<Plastic> getPlastics(Manufacturer manufacturer) {
        return PLASTICS.stream()
                .filter(mold -> mold.getManufacturer().getId().equals(manufacturer.getId()))
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getPlasticsDD() {
        return PLASTICS.stream()
                .map(DropdownOutputDto::fromDropdownInterface)
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getPlasticsDD(Manufacturer manufacturer) {
        return PLASTICS.stream()
                .filter(plastic -> plastic.getManufacturer().getId().equals(manufacturer.getId()))
                .map(DropdownOutputDto::fromDropdownInterface)
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getColorsDD() {
        return COLORS.stream()
                .map(DropdownOutputDto::fromDropdownInterface)
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getConditionsDD() {
        return CONDITIONS.stream()
                .map(DropdownOutputDto::fromDropdownInterface)
                .collect(Collectors.toList());
    }

    public static List<DropdownOutputDto> getMarkingsDD() {
        return MARKINGS.stream()
                .map(DropdownOutputDto::fromDropdownInterface)
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
}
