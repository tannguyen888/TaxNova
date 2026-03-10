package io.abc_def.kickstart_fx.tax;

public class TaxService {

    private final TaxCalculator taxCalculator;

    public TaxService() {
        this.taxCalculator = new TaxCalculator();
    }

    public double computeTax(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        double tax = taxCalculator.calculator(amount);

        return tax;
    }
}
