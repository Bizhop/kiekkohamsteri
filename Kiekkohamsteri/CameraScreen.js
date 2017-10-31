import React, { Component } from 'react';
import {
  Alert
} from 'react-native';
import { CameraKitCameraScreen } from 'react-native-camera-kit';
import App from './App'


export default class CameraScreen extends Component {

    constructor(props) {
        super(props);
        this.state = {
          app: undefined
        };
      }

    onBottomButtonPressed(event) {
        if(event.type === 'left') {
            this.setState(this.setState({app: App}))
        }
        else {
            const captureImages = JSON.stringify(event.captureImages);
            Alert.alert(
            `${event.type} button pressed`,
            `${captureImages}`,
            [
                { text: 'OK', onPress: () => console.log('OK Pressed') },
            ],
            { cancelable: false }
            )
        }
    }

    render() {
        if (this.state.app) {
            const CameraScreen = this.state.app;
            return <CameraScreen />;
        }
        return (
        <CameraKitCameraScreen
            actions={{ leftButtonText: 'Peruuta' }}
            onBottomButtonPressed={(event) => this.onBottomButtonPressed(event)}
            captureButtonImage={require('./images/cameraButton.png')}
        />
        );
    }
}