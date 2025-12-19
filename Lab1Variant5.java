import java.util.Locale;
import java.util.Scanner;

public class Lab1Variant5 {

    // Начальные условия задачи Коши
    private static final double X0 = 0.0;
    private static final double Y0 = -2.0;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите правый конец отрезка b (> 0): ");
        double b = scanner.nextDouble();

        System.out.print("Введите число шагов n (натуральное): ");
        int n = scanner.nextInt();

        double h = (b - X0) / n;

        // Массив узлов x_k
        double[] x = new double[n + 1];
        for (int k = 0; k <= n; k++) {
            x[k] = X0 + k * h;
        }

        // Точное решение в узлах
        double[] exact = new double[n + 1];
        for (int k = 0; k <= n; k++) {
            exact[k] = exactSolution(x[k]);
        }

        // Численные решения
        double[] euler = solveEuler(x, h);
        double[] heun = solveHeun(x, h);
        double[] rungeKutta4 = solveRungeKutta4(x, h);

        // Максимальные погрешности
        double maxAbsEuler = 0.0;
        double maxRelEuler = 0.0;

        double maxAbsHeun = 0.0;
        double maxRelHeun = 0.0;

        double maxAbsRK4 = 0.0;
        double maxRelRK4 = 0.0;

        System.out.println("k\tx_k\t\tphi(x_k)\t\tEuler\t\tHeun\t\tRK4\t\t" +
                "AbsEul\tRelEul\tAbsHeun\tRelHeun\tAbsRK4\tRelRK4");

        for (int k = 0; k <= n; k++) {
            double phi = exact[k];

            double eul = euler[k];
            double he = heun[k];
            double rk = rungeKutta4[k];

            double absEuler = Math.abs(phi - eul);
            double absHeun = Math.abs(phi - he);
            double absRK4 = Math.abs(phi - rk);

            double relEuler = (Math.abs(eul) > 1e-14)
                    ? absEuler / Math.abs(eul)
                    : 0.0;

            double relHeun = (Math.abs(he) > 1e-14)
                    ? absHeun / Math.abs(he)
                    : 0.0;

            double relRK4 = (Math.abs(rk) > 1e-14)
                    ? absRK4 / Math.abs(rk)
                    : 0.0;

            if (absEuler > maxAbsEuler) {
                maxAbsEuler = absEuler;
            }
            if (relEuler > maxRelEuler) {
                maxRelEuler = relEuler;
            }

            if (absHeun > maxAbsHeun) {
                maxAbsHeun = absHeun;
            }
            if (relHeun > maxRelHeun) {
                maxRelHeun = relHeun;
            }

            if (absRK4 > maxAbsRK4) {
                maxAbsRK4 = absRK4;
            }
            if (relRK4 > maxRelRK4) {
                maxRelRK4 = relRK4;
            }

            System.out.printf(
                    "%d\t%.6f\t\t%.6f\t\t%.6f\t\t%.6f\t\t%.6f\t\t" +
                            "%.3e\t%.3e\t%.3e\t%.3e\t%.3e\t%.3e%n",
                    k, x[k], phi, eul, he, rk,
                    absEuler, relEuler,
                    absHeun, relHeun,
                    absRK4, relRK4
            );
        }

        System.out.println("\nМаксимальные погрешности:");
        System.out.printf("Метод Эйлера:      Δ = %.3e, δ = %.3e%n", maxAbsEuler, maxRelEuler);
        System.out.printf("Метод Хойна:       Δ = %.3e, δ = %.3e%n", maxAbsHeun, maxRelHeun);
        System.out.printf("Метод Рунге-Кутты: Δ = %.3e, δ = %.3e%n", maxAbsRK4, maxRelRK4);

        scanner.close();
    }

    /**
     * Правая часть уравнения y' = f(x, y).
     * f(x, y) = 1 / sqrt(1 + x^2) + (x * y) / (1 + x^2)
     */
    private static double f(double x, double y) {
        double onePlusXSq = 1.0 + x * x;
        return 1.0 / Math.sqrt(onePlusXSq) + (x * y) / onePlusXSq;
    }

    /**
     * Аналитическое решение задачи Коши:
     * phi(x) = sqrt(1 + x^2) * (atan(x) - 2)
     */
    private static double exactSolution(double x) {
        return Math.sqrt(1.0 + (x * x)) * (Math.atan(x) - 2.0);
    }

    /**
     * Решение методом Эйлера.
     */
    private static double[] solveEuler(double[] x, double h) {
        int n = x.length;
        double[] y = new double[n];
        y[0] = Y0;

        for (int k = 0; k < n - 1; k++) {
            double xk = x[k];
            double yk = y[k];

            double slope = f(xk, yk);
            y[k + 1] = yk + h * slope;
        }

        return y;
    }

    /**
     * Решение методом Хойна.
     */
    private static double[] solveHeun(double[] x, double h) {
        int n = x.length;
        double[] y = new double[n];
        y[0] = Y0;

        for (int k = 0; k < n - 1; k++) {
            double xk = x[k];
            double yk = y[k];

            double k1 = f(xk, yk);
            double yPredict = yk + h * k1;

            double k2 = f(xk + h, yPredict);

            y[k + 1] = yk + (h / 2.0) * (k1 + k2);
        }

        return y;
    }

    /**
     * Решение методом Рунге–Кутты 4-го порядка.
     */
    private static double[] solveRungeKutta4(double[] x, double h) {
        int n = x.length;
        double[] y = new double[n];
        y[0] = Y0;

        for (int k = 0; k < n - 1; k++) {
            double xk = x[k];
            double yk = y[k];

            double k1 = f(xk, yk);
            double k2 = f(xk + h / 2.0, yk + h * k1 / 2.0);
            double k3 = f(xk + h / 2.0, yk + h * k2 / 2.0);
            double k4 = f(xk + h, yk + h * k3);

            y[k + 1] = yk + (h / 6.0) * (k1 + 2.0 * k2 + 2.0 * k3 + k4);
        }

        return y;
    }
}
