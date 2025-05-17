import java.util.concurrent.Semaphore;

public class SulfurikAsit_Uretme {

    static final int HEDEF_H2SO4 = 1000;
    static int uretilenH2SO4 = 0;

    static Semaphore mevcutO2 = new Semaphore(0);
    static Semaphore mevcutS = new Semaphore(0);
    static Semaphore mevcutH2O = new Semaphore(0);

    static Semaphore mevcutSO2 = new Semaphore(0);
    static Semaphore mevcutSO3 = new Semaphore(0);

    static Object lock = new Object();

    // Hedefe ulaşıldığında tüm üreticilerin durması için
    static volatile boolean uretimiDurdurma = false;

    public static void main(String[] args) {

    	System.out.println("H2SO4 Üretme Simülasyonu Başlatılıyor... \n");
    	
        // Element üreticiler
        Thread oksijenUretici1 = new Thread(new OksijenUretici(), "OksijenUretici1");
        Thread oksijenUretici2 = new Thread(new OksijenUretici(), "OksijenUretici2");
        Thread kükürtUretici = new Thread(new KukurtUretici(), "KukurtUretici");
        Thread suUretici = new Thread(new SuUretici(), "SuUretici");

        // Molekül üreticiler
        Thread so2Uretici = new Thread(new SO2Uretici(), "SO2Uretici");
        Thread so3Uretici = new Thread(new SO3Uretici(), "SO3Uretici");
        Thread h2so4Uretici = new Thread(new H2SO4Uretici(), "H2SO4Uretici");

        // Threadleri başlat
        oksijenUretici1.start();
        oksijenUretici2.start();
        kükürtUretici.start();
        suUretici.start();
        so2Uretici.start();
        so3Uretici.start();
        h2so4Uretici.start();

        try {
            h2so4Uretici.join(); // H2SO4 üretimi bitene kadar bekle
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Simülasyon tamamlandı : Toplam üretilen H2SO4 molekül miktarı = " + uretilenH2SO4);
        System.out.println("\n Tebrikler 1000 adet H2SO4 Molekülünü Basariyla Urettiniz.");
        System.exit(0);
    }
    
    static class OksijenUretici implements Runnable {
        @Override
        public void run() {
            while (!uretimiDurdurma) {
                mevcutO2.release();
                System.out.println(Thread.currentThread().getName() + ": 1 adet O2 üretildi.");
                sleep(10);
            }
        }
    }

    static class KukurtUretici implements Runnable {
        @Override
        public void run() {
            while (!uretimiDurdurma) {
                mevcutS.release();
                System.out.println("KukurtUretici: 1 adet S üretildi.");
                sleep(15);
            }
        }
    }

    static class SuUretici implements Runnable {
        @Override
        public void run() {
            while (!uretimiDurdurma) {
                mevcutH2O.release();
                System.out.println("SuUretici: 1 adet H2O üretildi.");
                sleep(20);
            }
        }
    }

    static class SO2Uretici implements Runnable {
        @Override
        public void run() {
            while (!uretimiDurdurma) {
                try {
                    mevcutS.acquire();
                    mevcutO2.acquire();
                    mevcutSO2.release();
                    System.out.println("SO2Uretici: 1 adet SO2 üretildi.");
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class SO3Uretici implements Runnable {
        @Override
        public void run() {
            while (!uretimiDurdurma) {
                try {
                    mevcutSO2.acquire();
                    mevcutO2.acquire();
                    mevcutSO3.release();
                    System.out.println("SO3Uretici: 1 adet SO3 üretildi.");
                    sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class H2SO4Uretici implements Runnable {
        @Override
        public void run() {
            while (!uretimiDurdurma) {
                try {
                    mevcutSO3.acquire();
                    mevcutH2O.acquire();

                    synchronized (lock) {
                        if (uretilenH2SO4 == HEDEF_H2SO4) {
                            uretimiDurdurma = true; // Hedefe ulaşıldığında tüm üretimi durdur
                            System.out.println("Hedeflenen H2SO4 üretimi tamamlandı.");
                            break; // Hedef üretim tamamlandığında dur
                        }
                        uretilenH2SO4++;
                        System.out.println("H2SO4Uretici: 1 adet H2SO4 üretildi. (Toplam: " + uretilenH2SO4 + ")");
                    }
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
