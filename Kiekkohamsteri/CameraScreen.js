import React, { Component } from 'react'
import { StyleSheet, Alert, View, Dimensions, ActivityIndicator } from 'react-native'
import { Button } from 'react-native-elements'
import RNFS from 'react-native-fs'
import Camera from 'react-native-camera'

import Api from './Api'

export default class CameraScreen extends Component {
  static navigationOptions = {
    title: 'Kamera',
  }

  constructor(props) {
    super(props)
    this.state = {
      user: props.navigation.state.params.user,
      newDisc: false,
      takingPicture: false,
    }
  }

  uploadImage(path) {
    const { navigate } = this.props.navigation
    RNFS.readFile(path, 'base64')
      .then(image => {
        Api.post(image, this.state.user.idToken)
          .then(response => {
            navigate('Kiekko', { selectedDisc: response, user: this.state.user })
          })
          .catch(error => {
            Alert.alert('Virhe kuvan lähetyksessä', error.message)
          })
          .done()
      })
      .done()
  }

  render() {
    return this.state.takingPicture ? (
      <View style={styles.container}>
        <ActivityIndicator size="large" />
      </View>
    ) : (
      <View style={styles.container}>
        <Camera
          ref={cam => {
            this.camera = cam
          }}
          style={styles.preview}
          aspect={Camera.constants.Aspect.fill}
          captureQuality={Camera.constants.CaptureQuality.preview}
          captureTarget={Camera.constants.CaptureTarget.temp}
        />
        <View>
          <Button
            raised
            rightIcon={{ name: 'camera-alt' }}
            backgroundColor="#009933"
            title="Ota kuva"
            disabled={this.state.takingPicture}
            onPress={() => this.takePicture()}
          />
        </View>
      </View>
    )
  }

  takePicture() {
    this.camera
      .capture({})
      .then(data => {
        this.setState({
          ...this.state,
          takingPicture: true,
        })
        this.uploadImage(data.path)
      })
      .catch(err => console.error(err))
  }
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'white',
  },
  preview: {
    height: Dimensions.get('window').width,
  },
})
