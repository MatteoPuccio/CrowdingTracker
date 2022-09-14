import React, { useEffect, useState } from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';


import BottomNavigation from './src/navigation/BottomNavigation';
import './src/constants/translations/IMLocalize';
import { NavigationContainer } from '@react-navigation/native';
import AuthenticationStack from './src/navigation/AuthenticationStack';

function App() {

  const MainStack = createNativeStackNavigator();



  return (
    <NavigationContainer>
      <MainStack.Navigator initialRouteName='AuthenticationStack'>
        <MainStack.Screen name='AuthenticationStack'
          options={{ headerShown: false }}
          children={() =>
            <AuthenticationStack />}
        />

        <MainStack.Screen name='BottomNavigation'
          options={{ headerShown: false }}
          children={() =>
            <BottomNavigation />}
        />

      </MainStack.Navigator>
    </NavigationContainer>
  );

};


export default App;
