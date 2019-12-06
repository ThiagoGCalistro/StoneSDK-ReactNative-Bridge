package com.stonesdk;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.List;

import stone.application.StoneStart;
import stone.application.enums.Action;
import stone.application.enums.InstalmentTransactionEnum;
import stone.application.enums.TypeOfTransactionEnum;
import stone.application.interfaces.StoneCallbackInterface;
import stone.database.transaction.TransactionObject;
import stone.environment.Environment;
import stone.providers.ActiveApplicationProvider;
import stone.providers.BluetoothConnectionProvider;
import stone.providers.TransactionProvider;
import stone.user.UserModel;
import stone.utils.PinpadObject;
import stone.utils.Stone;

public class StoneSDK extends ReactContextBaseJavaModule  {
    private static Boolean stoneProvider = false;
    private static Boolean stoneProviderPinpad = false;
    private static Boolean stoneTransaction = false;
    private static String stoneCode = "104765232";
    public static List<UserModel> userList;
    public static PinpadObject pinpadSelected;

//    BluetoothAdapter myBluetoothAdapter;
    String filter = "PAX";
    private static String pinpadMacAddress;
    private static String pinpadName;

    public StoneSDK(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void getStatus(
            Callback successCallback) {
        successCallback.invoke(null, stoneProvider);
    }

    @ReactMethod
    public void getStatusPinpad(
            Callback successCallback) {
        successCallback.invoke(null, stoneProviderPinpad);
    }

    @ReactMethod
    public void getStatusTransaction(
            Callback successCallback) {
        successCallback.invoke(null, stoneTransaction);
    }

    @ReactMethod
    public void initStoneSDK() {
        StoneStart.init(getReactApplicationContext());
        Stone.setAppName("StoneSDK");
        Stone.setEnvironment(Environment.SANDBOX);
    }

    @ReactMethod
    public void initStoneProvider() {
        userList = StoneStart.init(getReactApplicationContext());

        if (userList == null) {
            ActiveApplicationProvider activeApplicationProvider = new ActiveApplicationProvider(getReactApplicationContext());
            activeApplicationProvider.setConnectionCallback(new StoneCallbackInterface() {

                public void onSuccess() {
                    stoneProvider = true;
                }

                public void onError() {
                    stoneProvider = false;
                    activeApplicationProvider.getListOfErrors();
                }
            });
            activeApplicationProvider.activate(stoneCode);
        } else {
            stoneProvider = true;
        }
    }

    @ReactMethod
    public void initStoneProviderPinpad() {

        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();

        if(myDevice.isEnabled()) {
            for (BluetoothDevice pairedDevice : myDevice.getBondedDevices()) {
                if (pairedDevice.getName().contains(filter /*Like MI*/)) {
                    pinpadMacAddress = pairedDevice.getAddress();
                    pinpadName = pairedDevice.getName();
                    break;
                }
            }
        }

        pinpadSelected = new PinpadObject(pinpadName, pinpadMacAddress, false);

        BluetoothConnectionProvider bluetoothConnectionProvider = new BluetoothConnectionProvider(getReactApplicationContext(), pinpadSelected);
        bluetoothConnectionProvider.setConnectionCallback(new StoneCallbackInterface() {

            public void onSuccess() {
                stoneProviderPinpad = true;
            }

            public void onError() {
                stoneProviderPinpad = false;
                bluetoothConnectionProvider.getListOfErrors();
            }
        });
        bluetoothConnectionProvider.execute(); // Executa o provider de conexão bluetooth.
    }

    @ReactMethod
    public void initStoneTransaction(int value, String uuid, String installment, String method) {
        final TransactionObject transaction = new TransactionObject();

        transaction.setAmount(String.valueOf(value));
        transaction.setInitiatorTransactionKey(uuid);
        transaction.setInstalmentTransaction(InstalmentTransactionEnum.ONE_INSTALMENT);
        transaction.setTypeOfTransaction(TypeOfTransactionEnum.CREDIT);
        transaction.setCapture(true);

        final TransactionProvider provider = new TransactionProvider(getReactApplicationContext(), transaction, Stone.getUserModel(0), pinpadSelected);
        provider.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                stoneTransaction = true;
            }
            @Override
            public void onError() {
                stoneTransaction = false;
            }
        });

        provider.execute();

    }

    @Override
    public String getName() {
        return "StoneSDK";
    }

}
