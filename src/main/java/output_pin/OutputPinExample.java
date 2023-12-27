package output_pin;


import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.Pin;
import com.comert.gembedded.api.device.gpio.*;

public class OutputPinExample {

    public static void run() throws InterruptedException {

        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();
        try {
            deviceContext.setupDevice();

            final var outputPin = Pin.PIN_21;
            GPIOFactory gpioFactory = deviceContext.getGPIOFactoryInstance();
            OutputPin writer = gpioFactory.createOutputPin(
                    OutputPinConfigurator
                            .getBuilder()
                            .pin(outputPin)
                            .build()
            );

            for(int i=0; i<5;i++){
                writer.setHigh();
                Thread.sleep(50);
                writer.setLow();
                Thread.sleep(50);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            deviceContext.shutdownDevice();
        }
    }
}
