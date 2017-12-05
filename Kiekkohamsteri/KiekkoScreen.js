import React, { Component } from 'react'
import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  Alert,
  Image,
  Picker,
  TextInput,
  Switch,
  ActivityIndicator,
  Dimensions
} from 'react-native'
import { Button } from 'react-native-elements'
import R from 'ramda'

import Api from './Api'
import Helpers from './Helpers'

export default class KiekkoScreen extends Component {
  static navigationOptions = {
    title: 'Kiekko'
  }

  constructor(props) {
    const { params } = props.navigation.state
    super(props)

    const list = [
      'moldId',
      'muoviId',
      'variId',
      'valmId',
      'kunto',
      'tussit',
      'paino',
      'muuta',
      'hohto',
      'spessu',
      'dyed',
      'swirly',
      'loytokiekko',
      'itb',
      'myynnissa',
      'hinta'
    ]
    const predicate = (val, key) => R.contains(key, list)

    this.state = {
      user: params.user,
      selectedDisc: params.selectedDisc,
      dropdowns: null,
      kiekkoUpdate: R.pickBy(predicate, params.selectedDisc),
      buttonsDisabled: false
    }
  }

  componentDidMount() {
    this.initDropdowns()
  }

  initDropdowns = () => {
    Api.dropdowns(this.state.selectedDisc.valmId)
      .then(dropdowns => {
        this.updateState({ dropdowns: dropdowns })
      })
      .catch(err => {
        Alert.alert('Dropdownien haku epäonnistui', err.message)
      })
      .done()
  }

  updateState = obj => {
    this.setState(R.mergeDeepRight(this.state, obj))
  }

  updateKiekkoUpdate = obj => {
    this.setState({
      ...this.state,
      kiekkoUpdate: R.mergeDeepRight(this.state.kiekkoUpdate, obj)
    })
  }

  render() {
    return this.state.selectedDisc && this.state.dropdowns ? (
      <ScrollView style={styles.scrollContainer}>
        <View style={styles.header}>
          <Text>{Helpers.discBasics(this.state.selectedDisc)}</Text>
          <Text style={{ color: 'grey' }}>{Helpers.discStats(this.state.selectedDisc)}</Text>
        </View>
        <View style={styles.inputRow}>
          <Image
            source={{ uri: `${Helpers.imagesUrl}t_kiekko/${this.state.selectedDisc.kuva}` }}
            style={styles.discImage}
          />
        </View>
        <View style={styles.inputRow}>
          <Text style={styles.label}>Valmistaja</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.valmId}
            onValueChange={(itemValue, itemIndex) => {
              Api.dropdowns(itemValue)
                .then(dropdowns => {
                  this.updateState({ dropdowns: dropdowns })
                  this.updateKiekkoUpdate({ valmId: itemValue })
                })
                .catch(() => {
                  console.log('Dropdownien haku epäonnistui')
                })
                .done()
            }}
          >
            {this.state.dropdowns.valms.map(v => (
              <Picker.Item key={v.id} label={v.valmistaja} value={v.id} />
            ))}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text style={styles.label}>Mold</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.moldId}
            onValueChange={(itemValue, itemIndex) => this.updateKiekkoUpdate({ moldId: itemValue })}
          >
            {this.state.dropdowns.molds.map(v => (
              <Picker.Item key={v.id} label={v.kiekko} value={v.id} />
            ))}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text style={styles.label}>Muovi</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.muoviId}
            onValueChange={(itemValue, itemIndex) =>
              this.updateKiekkoUpdate({ muoviId: itemValue })
            }
          >
            {this.state.dropdowns.muovit.map(v => (
              <Picker.Item key={v.id} label={v.muovi} value={v.id} />
            ))}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text style={styles.label}>Väri</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.variId}
            onValueChange={(itemValue, itemIndex) => this.updateKiekkoUpdate({ variId: itemValue })}
          >
            {this.state.dropdowns.varit.map(v => (
              <Picker.Item key={v.id} label={v.vari} value={v.id} />
            ))}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text style={styles.label}>Kunto</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.kunto}
            onValueChange={(itemValue, itemIndex) => this.updateKiekkoUpdate({ kunto: itemValue })}
          >
            {this.state.dropdowns.kunto.map(v => (
              <Picker.Item key={v.id} label={v.nimi} value={v.id} />
            ))}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text style={styles.label}>Tussit</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.tussit}
            onValueChange={(itemValue, itemIndex) => this.updateKiekkoUpdate({ tussit: itemValue })}
          >
            {this.state.dropdowns.tussit.map(v => (
              <Picker.Item key={v.id} label={v.nimi} value={v.id} />
            ))}
          </Picker>
        </View>
        <View style={styles.inputTextContainer}>
          <Text style={styles.label}>Paino</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateKiekkoUpdate({ paino: text })}
            value={this.state.kiekkoUpdate.paino.toString()}
            keyboardType="numeric"
          />
        </View>
        {this.myCheckBox('Hohtava', 'hohto')}
        {this.myCheckBox('Spesiaali', 'spessu')}
        {this.myCheckBox('Värjätty', 'dyed')}
        {this.myCheckBox('Swirly', 'swirly')}
        {this.myCheckBox('Löytökiekko', 'loytokiekko')}
        {this.myCheckBox('In the bag', 'itb')}
        <View style={styles.inputTextContainer}>
          <Text style={styles.label}>Muuta</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateKiekkoUpdate({ muuta: text })}
            value={this.state.kiekkoUpdate.muuta}
          />
        </View>
        {this.myCheckBox('Myynnissä', 'myynnissa')}
        {this.state.kiekkoUpdate.myynnissa && (
          <View style={styles.inputTextContainer}>
            <Text style={styles.label}>Hinta</Text>
            <TextInput
              style={styles.textInput}
              onChangeText={text => this.updateKiekkoUpdate({ hinta: text })}
              value={this.state.kiekkoUpdate.hinta.toString()}
              keyboardType="numeric"
            />
          </View>
        )}
        <Button
          raised
          rightIcon={{ name: 'file-upload' }}
          backgroundColor="#0077ff"
          title="Päivitä"
          onPress={() => this.updateDisc()}
          disabled={this.state.buttonsDisabled}
        />
        <Button
          raised
          rightIcon={{ name: 'delete' }}
          backgroundColor="#ff3333"
          title="Poista"
          onPress={() =>
            Alert.alert('Varoitus', 'Kiekko poistetaan pysyvästi', [
              { text: 'Peruuta' },
              { text: 'Poista', onPress: () => this.deleteDisc() }
            ])
          }
          disabled={this.state.buttonsDisabled}
        />
      </ScrollView>
    ) : (
      <View style={styles.container}>
        <ActivityIndicator size="large" />
      </View>
    )
  }

  myCheckBox(title, value) {
    return (
      <View style={styles.inputRow}>
        <Text style={styles.label}>{title}</Text>
        <View style={styles.checkbox}>
          <Switch
            value={R.path([value], this.state.kiekkoUpdate)}
            onValueChange={() =>
              this.updateKiekkoUpdate(
                R.zipObj([value], [!R.path([value], this.state.kiekkoUpdate)])
              )
            }
          />
        </View>
      </View>
    )
  }

  updateDisc() {
    this.updateState({ buttonsDisabled: true })
    const { navigate } = this.props.navigation
    Api.put({
      token: this.state.user.idToken,
      kiekko: this.state.kiekkoUpdate,
      id: this.state.selectedDisc.id
    })
      .then(() => {
        navigate('Lista', { user: this.state.user })
      })
      .catch(err => {
        Alert.alert('Kiekon päivitys epäonnistui', err.message)
      })
      .done()
  }

  deleteDisc() {
    this.updateState({ buttonsDisabled: true })
    const { navigate } = this.props.navigation
    if (Api.delete(this.state.selectedDisc.id, this.state.user.idToken)) {
      navigate('Lista', { user: this.state.user })
    } else {
      Alert.alert('Kiekon poisto epäonnistui')
    }
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#FFFFFF'
  },
  scrollContainer: {
    flex: 1,
    alignItems: 'flex-start',
    height: 1000,
    backgroundColor: '#FFFFFF'
  },
  header: {
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    color: 'black',
    fontSize: 24
  },
  discImage: {
    height: Dimensions.get('window').width,
    width: Dimensions.get('window').width
  },
  inputRow: {
    flexDirection: 'row',
    alignItems: 'flex-start'
  },
  label: {
    flex: 1
  },
  inputTextContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 5
  },
  textInput: {
    height: 40,
    flex: 4
  },
  picker: {
    flex: 4
  },
  checkbox: {
    flex: 4,
    flexDirection: 'row',
    alignItems: 'flex-start'
  }
})
