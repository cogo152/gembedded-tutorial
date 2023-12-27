package pwm;

import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.Pin;
import com.comert.gembedded.api.device.pwm.*;

public class PWMPinExample {

    public static void run() throws InterruptedException {

        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();
        try {
            deviceContext.setupDevice();

            final Pin pwmPin = Pin.PIN_19;
            final int range = 20000;
            final int degree0 = 2000;
            final int degree180 = 10000;
            final int degreeSleep = 5;
            final int playTime = 10;

            final int rate = (int) ((degree180 - degree0) / 180.0);
            final int defaultSleep = degreeSleep * 50;

            PWMFactory pwmFactory = deviceContext.getPWMFactoryInstance();
            PWMPin pulser = pwmFactory.createPWMPin(
                    PWMPinConfigurator
                            .getBuilder()
                            .pin(pwmPin)
                            .mode(PWMMode.MARK_SPACE)
                            .polarity(PWMPolarity.LOW_HIGH)
                            .silence(PWMSilence.LOW)
                            .range(range)
                            .build());

            pulser.enable();

            pulser.writeData(degree0);

            Thread.sleep(defaultSleep);

            for (int i = 0; i < playTime; i++) {

                for (int degree = degree0; degree <= degree180; degree = degree + rate) {
                    pulser.writeData(degree);
                    Thread.sleep(degreeSleep);
                }

                Thread.sleep(defaultSleep);

                for (int degree = degree180; degree >= degree0; degree = degree - rate) {
                    pulser.writeData(degree);
                    Thread.sleep(degreeSleep);
                }

                Thread.sleep(defaultSleep);

            }

            pulser.writeData(degree0);

            Thread.sleep(defaultSleep);

            pulser.disable();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            deviceContext.shutdownDevice();
        }
    }

}
