package eu.the5zig.mod.server.octc;

import eu.the5zig.mod.server.GameMode;

public class ServerOCC {
    public static class Arena extends GameMode {
        private String subMode;
        private String map;

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

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }
    }
}
