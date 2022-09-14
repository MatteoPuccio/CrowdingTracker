import { deleteItemEncrypted, getItemEncrypted, storeItemEncrypted } from "../utils/common";

export const getAccessToken = () => { return accessToken };

export const doLogin = async (credentials) => {

    let loginState = {
        successful: false,
        userNotFound: false,
        wrongPassword: false,
    }

    let token = await fetch('http://10.0.2.2:8080/doLogin', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
        .then((response) => response.json())
        .then((json) => {
            console.log(JSON.stringify(json));

            if (json.userNotFound)
                loginState.userNotFound = true;
            else if (json.wrongPassword) {
                loginState.wrongPassword = true;
            } else {
                loginState.successful = true;
                accessToken = {
                    token: json.user.accessToken,
                    userId: json.user.userId,
                };
                return accessToken;
            }
        });

    await storeItemEncrypted('@access_token', token);
    return loginState;
}

export async function doLoginToken() {

    let token = await getItemEncrypted('@access_token');

    if (token != null) {
        fetch('http://10.0.2.2:8080/tokenLogin', {
            method: 'POST',
            headers: {
                Accept: 'text/plain',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                token: token.token,
                userId: token.userId
            })
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.userNotFound) {
                    deleteItemEncrypted('@access_token');
                    console.log("User not found");
                }
                else if (json.tokenExpired || json.tokenIdMismatch || json.tokenNotFound) {
                    deleteItemEncrypted('@access_token');
                    console.log("Invalid Token!");
                } else {
                    console.log("Successful login!");
                    accessToken = token;
                }
            });
    }
}

export const doLogout = () => {
    deleteItemEncrypted('@access_token');
}

export const doSignUp = async (credentials) => {
    let signUpState = {
        successful: false,
        emailAlreadyUsed: false,
        emailNotConfirmed: false
    }

    await fetch('http://10.0.2.2:8080/doSignUp', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
        .then((response) => response.json())
        .then((json) => {
            console.log(JSON.stringify(json));

            if (json.emailAlreadyUsed)
                signUpState.emailAlreadyUsed = true;
            else if (json.emailNotConfirmed)
                signUpState.emailNotConfirmed = true;
            else
                signUpState.successful = true;
        });

    if (signUpState.emailNotConfirmed || signUpState.successful)
        await storeItemEncrypted('@registration_in_progress', credentials.email)
    return signUpState;
}

export const doConfirmEmail = async ({ token, locale }) => {

    let email = await getItemEncrypted('@registration_in_progress');

    let confirmEmailState = {
        successful: false,
        wrongCode: false,
        expiredCode: false,
    }

    await fetch('http://10.0.2.2:8080/doConfirmEmail', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            'email': email,
            'token': token,
            'locale': locale
        })
    })
        .then((response) => response.json())
        .then((json) => {
            console.log(JSON.stringify(json));

            if (json.wrongCode)
                confirmEmailState.wrongCode = true;
            else if (json.expiredCode) {
                confirmEmailState.expiredCode = true;
                resendRegistrationEmail();
            } else {
                confirmEmailState.successful = true;
                deleteItemEncrypted('@registration_in_progress')
            }
        });

    return confirmEmailState;
}

export const resendRegistrationEmail = async () => {
    let email = await getItemEncrypted('@registration_in_progress');

    fetch('http://10.0.2.2:8080/sendConfirmEmailAgain', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: email
    })
}

export const forgotPassword = () => {

}