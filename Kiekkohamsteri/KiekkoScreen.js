import React, { Component } from 'react'
import { StyleSheet, Text, View, ScrollView, Alert, Image, Picker, TextInput } from 'react-native'
import { Button, CheckBox } from 'react-native-elements'
import R from 'ramda'

import Api from './Api'
import Helpers from './Helpers'

export default class KiekkoScreen extends Component {
  static navigationOptions = {
    title: 'Kiekko'
  }

  constructor(props) {
    const { params } = props.navigation.state
    console.log(params)
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
    this.initDropdowns(params.selectedDisc)
  }

  initDropdowns = item => {
    Api.dropdowns(item.valmId)
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
        <Text>{Helpers.discBasics(this.state.selectedDisc)}</Text>
        <Text>{Helpers.discStats(this.state.selectedDisc)}</Text>
        <Image
          source={{ uri: `${Helpers.imagesUrl}t_kiekko/${this.state.selectedDisc.kuva}` }}
          style={styles.discImage}
        />
        <View style={styles.inputRow}>
          <Text>Valmistaja</Text>
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
          <Text>Mold</Text>
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
          <Text>Muovi</Text>
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
          <Text>Väri</Text>
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
          <Text>Kunto</Text>
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
          <Text>Tussit</Text>
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
          <Text>Paino</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateKiekkoUpdate({ paino: text })}
            value={this.state.kiekkoUpdate.paino.toString()}
            keyboardType="numeric"
          />
        </View>
        {this.myCheckBox('Hohto', 'hohto')}
        {this.myCheckBox('Spesiaali', 'spessu')}
        {this.myCheckBox('Värjätty', 'dyed')}
        {this.myCheckBox('Swirly', 'swirly')}
        {this.myCheckBox('Löytökiekko', 'loytokiekko')}
        {this.myCheckBox('In the bag', 'itb')}
        <View style={styles.inputTextContainer}>
          <Text>Muuta</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateKiekkoUpdate({ muuta: text })}
            value={this.state.kiekkoUpdate.muuta}
          />
        </View>
        {this.myCheckBox('Myynnissä', 'myynnissa')}
        {this.state.kiekkoUpdate.myynnissa && (
          <View style={styles.inputTextContainer}>
            <Text>Hinta</Text>
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
      <View>
        <Text>Haetaan tietoja...</Text>
      </View>
    )
  }

  myCheckBox(title, value) {
    return (
      <CheckBox
        title={title}
        checked={R.path([value], this.state.kiekkoUpdate)}
        onIconPress={() =>
          this.updateKiekkoUpdate(R.zipObj([value], [!R.path([value], this.state.kiekkoUpdate)]))
        }
        onPress={() =>
          this.updateKiekkoUpdate(R.zipObj([value], [!R.path([value], this.state.kiekkoUpdate)]))
        }
      />
    )
  }

  updateDisc() {
    this.updateState({ buttonsDisabled: true })
    const { navigate } = this.props.navigation
    if (Api.login(this.state.user.idToken)) {
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
  }

  deleteDisc() {
    this.updateState({ buttonsDisabled: true })
    const { navigate } = this.props.navigation
    if (Api.login(this.state.user.idToken)) {
      if (Api.delete(this.state.selectedDisc.id, this.state.user.idToken)) {
        navigate('Lista', { user: this.state.user })
      } else {
        Alert.alert('Kiekon poisto epäonnistui')
      }
    }
  }
}

const styles = StyleSheet.create({
  scrollContainer: {
    flex: 1,
    alignItems: 'flex-start',
    height: 1000
  },
  discImage: {
    width: 300,
    height: 300
  },
  inputRow: {
    flexDirection: 'row',
    alignItems: 'flex-start'
  },
  inputTextContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 5
  },
  textInput: {
    height: 40,
    width: 300
  },
  picker: {
    width: 150
  }
})
