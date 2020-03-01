package eu.the5zig.mod.server.octc;

import eu.the5zig.mod.server.GameMode;

public class ServerOCC {
    public static class Arena extends GameMode {
        private String subMode;

        @Override
        public String getName() {
            return "Arena";
        }

        public String getSubMode() {
            return subMode;
        }

        public void setSubMode(String subMode) {
            this.subMode = subMode;
        }
    }
}
