package io.abc_def.kickstart_fx.tax;

public class TaxCalculator {

    ContainertaxRate TAX_RATE = new ContainertaxRate(0.10);

    public double calculator(double amount) {
        return amount * TAX_RATE.getTaxRate();
    }

}

class ContainertaxRate {

    private double taxRate;

    public ContainertaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxRate() {
        return taxRate;
    }

}