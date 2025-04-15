package vinnsla;

public class CasinoSession {
    private static final PlayerProfile sharedProfile = new PlayerProfile(1000);

    public static PlayerProfile getProfile() {
        return sharedProfile;
    }
}