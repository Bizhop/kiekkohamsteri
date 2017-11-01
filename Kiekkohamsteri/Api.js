import { Alert } from 'react-native'

const base = 'https://kiekkohamsteri-backend.herokuapp.com/api/'

const Api = {
    async getRaw(endpoint) {
        const url = base + endpoint
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application-json',
            }
        })
        const responseJson = await response.json()
        return responseJson
    }
}

export default Api
