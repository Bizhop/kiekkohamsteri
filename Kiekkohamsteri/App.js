import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Alert
} from 'react-native';
import { CameraKitCamera } from 'react-native-camera-kit';

export default class App extends Component<{}> {
  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={() => this.onCheckCameraAuthoPressed()}>
          <Text style={styles.buttonText}>
            Camera Authorization Status
          </Text>
        </TouchableOpacity>
      </View>
    );
  }
}

async onCheckCameraAuthoPressed => {
  const success = await CameraKitCamera.checkDeviceCameraAuthorizationStatus();
  if (success) {
    Alert.alert('You have permission')
  }
  else {
    Alert.alert('No permission')
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  }
});
