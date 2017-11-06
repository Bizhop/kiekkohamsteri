import { Alert } from 'react-native'

const base = 'https://kiekkohamsteri-backend.herokuapp.com/api/'

const Api = {
    async get(endpoint, user) {
        const url = base + endpoint
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application-json',
                'Authorization': user.idToken,
            }
        })
        const json = await response.json()
        return json
    },
    async ping() {
        const url = base + 'ping'
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'text-plain',
            }
        })
        return response
    },
    async login(user) {
        const url = base + 'auth/login'
        await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application-json',
                'Authorization': user.idToken,
            }
        })
        .then((response) => {
            return response.email === user.email
        })
    }
}

export default Api
