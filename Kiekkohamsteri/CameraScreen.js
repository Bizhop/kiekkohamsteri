import React, { Component } from 'react';
import {
  Alert
} from 'react-native';
import { CameraKitCameraScreen } from 'react-native-camera-kit';
import App from './App'
import RNFS from 'react-native-fs'
import Api from './Api'

export default class CameraScreen extends Component {
    static navigationOptions = {
        title: 'Kamera'
    }

    constructor(props) {
        super(props);
        console.log(props)
        this.state = {
          user: props.navigation.state.params.user,
          newDisc: false
        }
      }

    onBottomButtonPressed(event) {
        this.uploadFirstImage(event.captureImages)
    }

    async uploadFirstImage(images) {
        const first = images[0]
        await RNFS.readFile(first.uri, 'base64').then((image) => {
            Api.post(first.name, image, this.state.user.idToken)
            .then((response) => {
                console.log(response)
                Alert.alert('Go forward request')
            })
            .catch((error) => {
                console.log(error)
            })
            .done()
        })
    }

    render() {
        return (
            <CameraKitCameraScreen
                onBottomButtonPressed={(event) => this.onBottomButtonPressed(event)}
                captureButtonImage={require('./images/cameraButton.png')}
            />
        )
    }
}