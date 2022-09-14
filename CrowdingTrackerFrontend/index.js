/**
 * @format
 */

import { AppRegistry } from 'react-native';
import App from './App';
import { name as appName } from './app.json';
import messaging from '@react-native-firebase/messaging';

messaging().setBackgroundMessageHandler(async remoteMessage => { });

messaging().getToken().then((token) => console.log(token));


AppRegistry.registerComponent(appName, () => App);
