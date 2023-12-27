package i2c;

import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.DeviceIOException;
import com.comert.gembedded.api.device.Pin;
import com.comert.gembedded.api.device.i2c.I2CBus;
import com.comert.gembedded.api.device.i2c.I2CMaster;
import com.comert.gembedded.api.device.i2c.I2CMasterConfigurator;
import com.comert.gembedded.api.device.i2c.I2CMasterFactory;

public class I2CExample {

    public static void run() throws InterruptedException {

        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();
        try {
            deviceContext.setupDevice();

            final byte ADCSlave = (byte) 0x4b;
            final byte ADCSlaveChannel0 = (byte) 0b10000000;

            byte[] dataToSend = new byte[1];
            int[] dataToReceive = new int[1];

            dataToSend[0] = ADCSlaveChannel0;

            I2CMasterFactory i2CMasterFactory = deviceContext.getI2CMasterFactoryInstance();
            I2CMaster master = i2CMasterFactory.createI2CMaster(
                    I2CMasterConfigurator
                            .getBuilder()
                            .bus(I2CBus.BUS_1)
                            .busClockInHertz(3400000)
                            .slaveWaitClockTimeout(0x0040)
                            .sdaPin(Pin.PIN_2)
                            .sclPin(Pin.PIN_3, 0x0030, 0x0030)
                            .build());

            for (int i = 0; i < 5; i++) {
                try {
                    master.sendAndReceiveDataWithException(ADCSlave, dataToSend, dataToReceive);
                    System.out.println(dataToReceive[0]);
                } catch (DeviceIOException deviceIOException) {
                    System.out.println(deviceIOException.getMessage());
                }
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            deviceContext.shutdownDevice();
        }

    }
}
