package Day6_7.entities;

public enum UserStatus {

        ONLINE("Online"),
        OFFLINE("Offline"),
        AWAY("Away"),
        BUSY("Busy");

        private final String displayName;

        UserStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
}
