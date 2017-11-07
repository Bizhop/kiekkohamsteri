import React, { Component } from 'react';
import {
  Alert
} from 'react-native';
import { CameraKitCameraScreen } from 'react-native-camera-kit';
import App from './App'
import RNFS from 'react-native-fs'
import Api from './Api'

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
            this.uploadFirstImage(event.captureImages)
        }
    }

    async uploadFirstImage(images) {
        const first = images[0]
        await RNFS.readFile(first.uri, 'base64').then((image) => {
            Api.upload(first.name, image)
            .then((response) => {
                console.log(response)
            })
            .catch((error) => {
                console.log(error)
            })
        })
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