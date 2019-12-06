/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { useState, useEffect, Component } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,
  NativeModules
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

 function App(){
   let stats = "Não foi"
   const [status, setStatus] = useState(false);
   const [statusPinpad, setStatusPinpad] = useState(false);
   const [statusTransaction, setStatusTransaction] = useState(false);
    useEffect(() => {
     // NativeModules.StoneSDK.initStoneSDK();
     // NativeModules.StoneSDK.initStoneProvider();
     NativeModules.StoneSDK.getStatus((error, value ) => { setStatus(value) })
     NativeModules.StoneSDK.getStatusPinpad((error, value ) => { setStatusPinpad(value) })
     NativeModules.StoneSDK.getStatusTransaction((error, value ) => { setStatusTransaction(value) })
   }, [status]);

    return (
      <View>
        <Text>TESTE SDK STONE - {status.toString()}</Text>
        <Button onPress={() => NativeModules.StoneSDK.initStoneSDK()} title="Ativar SDK"></Button>
        <Button onPress={() => NativeModules.StoneSDK.initStoneProvider()} title="ATIVAR PROVIDER"></Button>
        <Button onPress={() => {NativeModules.StoneSDK.getStatus((error, value ) => { setStatus(value) })}} title="STATUS PROVIDER"></Button>

        <Text>TESTE PROVIDER PINPAD BT - {statusPinpad.toString()}</Text>
        <Button onPress={() => NativeModules.StoneSDK.initStoneProviderPinpad()} title="Conectar pinpad BT"></Button>
        <Button onPress={() => {NativeModules.StoneSDK.getStatusPinpad((error, value ) => { setStatusPinpad(value) })}} title="STATUS PINPAD"></Button>

        <Text>TESTE TRANSACTION - {statusTransaction.toString()}</Text>
        <Button onPress={() => NativeModules.StoneSDK.initStoneTransaction(1000,"sdadawd-awdwada", "1", "credit")} title="Ativar SDK"></Button>
        <Button onPress={() => {NativeModules.StoneSDK.getStatusTransaction((error, value ) => { setStatusTransaction(value) })}} title="STATUS TRANSACTION"></Button>
      </View>
    );
}

export default App;
