package input_pin;

import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.Pin;
import com.comert.gembedded.api.device.gpio.*;

public class InputPinExample {

    public static void run() throws InterruptedException {

        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();
        try {
            deviceContext.setupDevice();

            final var outputPin = Pin.PIN_20;
            final var inputPin = Pin.PIN_21;

            GPIOFactory gpioFactory = deviceContext.getGPIOFactoryInstance();

            OutputPin writer = gpioFactory.createOutputPin(
                    OutputPinConfigurator
                            .getBuilder()
                            .pin(outputPin)
                            .build());
            InputPin reader = gpioFactory.createInputPin(
                    InputPinConfigurator
                            .getBuilder()
                            .pin(inputPin)
                            .pullUpDown(PullUpDown.NONE)
                            .build());

            writer.setHigh();
            System.out.println(reader.isHigh());
            Thread.sleep(1000);
            writer.setLow();
            System.out.println(reader.isLow());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            deviceContext.shutdownDevice();
        }
    }
}
