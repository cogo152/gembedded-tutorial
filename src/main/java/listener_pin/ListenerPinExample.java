package listener_pin;

import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.Pin;
import com.comert.gembedded.api.device.gpio.*;

class CallBack implements ListenerCallBack {

    private volatile boolean risingOccurred;
    private volatile boolean fallingOccurred;
    private volatile boolean timeoutOccurred;

    private volatile long timeStamp;

    @Override
    public void onRising(long timeStamp) {
        risingOccurred = true;
        this.timeStamp = timeStamp;
    }

    @Override
    public void onFalling(long timeStamp) {
        fallingOccurred = true;
        this.timeStamp = timeStamp;
    }

    @Override
    public void onTimeout() {
        timeoutOccurred = true;
    }

    @Override
    public void onError() {
        throw new RuntimeException("Error should not be raised");
    }

    public boolean isRisingOccurred() {
        boolean _risingOccurred = risingOccurred;
        risingOccurred = false;
        return _risingOccurred;
    }

    public boolean isFallingOccurred() {
        boolean _fallingOccurred = fallingOccurred;
        fallingOccurred = false;
        return _fallingOccurred;
    }

    public boolean isTimeoutOccurred() {
        boolean _timeoutOccurred = timeoutOccurred;
        timeoutOccurred = false;
        return _timeoutOccurred;
    }

    public long getTimeStamp() {
        long _timeStamp = timeStamp;
        timeStamp = 0;
        return _timeStamp;
    }
}

public class ListenerPinExample {

    public static void run() throws InterruptedException {

        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();
        try {
            deviceContext.setupDevice();

            final var triggerPin = Pin.PIN_20;
            final var listenerPin = Pin.PIN_21;
            final var callBack = new CallBack();

            final var timeout = 100;

            GPIOFactory gpioFactory = deviceContext.getGPIOFactoryInstance();
            ListenerPin listener = gpioFactory.createListenerPin(
                    ListenerPinConfigurator
                            .getBuilder()
                            .pin(listenerPin)
                            .eventStatus(Event.SYNCHRONOUS_BOTH)
                            .timeoutInMilSec(timeout)
                            .callBack(callBack)
                            .build());
            OutputPin trigger = gpioFactory.createOutputPin(
                    OutputPinConfigurator
                            .getBuilder()
                            .pin(triggerPin)
                            .build());

            System.out.println();
            System.out.println("***STARTING***");
            listener.start();
            System.out.println("Is started : " + listener.isStarted());

            System.out.println();
            System.out.println("***TRIG RISING***");
            trigger.setHigh();
            Thread.sleep(timeout / 2);
            System.out.println(
                    "Is rising occurred :" + callBack.isRisingOccurred() + ", Timestamp : " + callBack.getTimeStamp());
            System.out.println("Is falling occurred :" + callBack.isFallingOccurred());
            System.out.println("Is timeout occurred :" + callBack.isTimeoutOccurred());

            System.out.println();
            System.out.println("***TRIG FALLING***");
            trigger.setLow();
            Thread.sleep(timeout / 2);
            System.out.println("Is falling occurred :" + callBack.isFallingOccurred() + ", Timestamp : "
                    + callBack.getTimeStamp());
            System.out.println("Is rising occurred :" + callBack.isRisingOccurred());
            System.out.println("Is timeout occurred :" + callBack.isTimeoutOccurred());

            System.out.println();
            System.out.println("***TIMEOUT***");
            Thread.sleep(timeout * 2);
            System.out.println("Is timeout occurred :" + callBack.isTimeoutOccurred());
            System.out.println("Is rising occurred :" + callBack.isRisingOccurred());
            System.out.println("Is falling occurred :" + callBack.isFallingOccurred());

            System.out.println();
            System.out.println("***SUSPENDING***");
            listener.suspend();
            trigger.setHigh();
            Thread.sleep(timeout / 2);
            System.out.println("Is rising occurred :" + callBack.isRisingOccurred());
            trigger.setLow();
            Thread.sleep(timeout / 2);
            System.out.println("Is falling occurred :" + callBack.isFallingOccurred());
            Thread.sleep(timeout * 2);
            System.out.println("Is timeout occurred :" + callBack.isTimeoutOccurred()); // Resolve without timeout
            System.out.println("Is suspended : " + listener.isSuspended());

            System.out.println();
            System.out.println("***RESUMING***");
            listener.resume();
            trigger.setHigh();
            Thread.sleep(timeout / 2);
            System.out.println(
                    "Is rising occurred :" + callBack.isRisingOccurred() + ", Timestamp : " + callBack.getTimeStamp());
            trigger.setLow();
            Thread.sleep(timeout / 2);
            System.out.println("Is falling occurred :" + callBack.isFallingOccurred() + ", Timestamp : "
                    + callBack.getTimeStamp());
            Thread.sleep(timeout * 2);
            System.out.println("Is timeout occurred :" + callBack.isTimeoutOccurred());
            System.out.println("Is resumed : " + listener.isResumed());

            System.out.println();
            System.out.println("***TERMINATING***");
            listener.terminate();
            trigger.setHigh();
            Thread.sleep(timeout / 2);
            System.out.println("Is rising occurred :" + callBack.isRisingOccurred());
            trigger.setLow();
            Thread.sleep(timeout / 2);
            System.out.println("Is falling occurred :" + callBack.isFallingOccurred());
            Thread.sleep(timeout * 2);
            System.out.println("Is timeout occurred :" + callBack.isTimeoutOccurred()); // Resolve without timeout
            System.out.println("Is terminated : " + listener.isTerminated());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            deviceContext.shutdownDevice();
        }
    }
}
