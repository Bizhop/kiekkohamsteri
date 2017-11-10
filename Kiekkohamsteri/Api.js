import { Alert } from 'react-native'

const base = 'https://kiekkohamsteri-backend.herokuapp.com/api/'

const Api = {
    async get(endpoint, token) {
        const url = base + endpoint
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token,
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
                'Content-Type': 'text/plain',
            }
        })
        return response
    },
    async login(token) {
        const url = base + 'auth/login'
        await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token,
            }
        })
        .then((response) => {
            return response.email ? true : false
        })
        .catch((error) => {
            return false
        })
    },
    async upload(name, data){
        const url = base + 'upload'
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                data: `data:image/jpeg;base64,${data}`,
                name: name,
            })
        })
        const json = await response.json()
        return json
    },
    async dropdowns(valmId) {
        const url = base + 'dropdown?valmId='+valmId
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        const json = await response.json()
        return json
    }
}

export default Api
