package io.abc_def.kickstart_fx.revenue;

public class RevenueViewModel {
    private final RevenueService revenueService;

    public RevenueViewModel(RevenueService revenueService) {
        this.revenueService = revenueService;

    }

    public void getAllRecipts() {
        return revenueService.getAllReceipts();
    }
}
