package net.spudacious5705.shops.properties;

public enum PermissionLevel {
    SERVER_ADMIN(true, false,false,false,false,false,false),
    OWNER(true,true,true,true,true,true,true),
    MANAGER(false,true,true,true,true,false,true),
    SUPERVISOR(false,false,true,true,true,false,true),
    CLERK(false,false,true,false,false,false,true),
    CUSTOMER(false,false,false,false,false,false,false);

    final boolean breakBlock;
    final boolean editPermissions;
    final boolean importStock;
    final boolean importCurrency;
    final boolean takeItems;
    final boolean editTrades;
    final boolean viewShopScreen;

        PermissionLevel(
                boolean breakBlock,
                boolean editPermissions,
                boolean importStock,
                boolean importCurrency,
                boolean takeItems,
                boolean editTrades,
                boolean viewShopScreen
        ) {
            this.breakBlock = breakBlock;
            this.editPermissions = editPermissions;
            this.importStock = importStock;
            this.importCurrency = importCurrency;
            this.takeItems = takeItems;
            this.editTrades = editTrades;
            this.viewShopScreen = viewShopScreen;
        }

        // Optionally, add getter methods to access the field values
        public boolean canBreakBlock() {
            return breakBlock;
        }

        public boolean canImportStock() {
            return importStock;
        }

        public boolean canImportCurrency() {
            return importCurrency;
        }

        public boolean canTakeItems() {
            return takeItems;
        }

        public boolean canEditTrades() {
            return editTrades;
        }

        public boolean canViewShopScreen() {
            return viewShopScreen;
        }

    public boolean canEditPermissions() {
        return editPermissions;
    }

}
