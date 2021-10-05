/**
 * @Author Sorin
 * Version 1.3
 * As of 12/03/2019
 * Checks card is valid.
 *
 * @param {string} cardname The type of card.
 * @param {string} cardnumber The numbers on the front of the card.
 * @param {string} expiry the expiry date of the card in the format: mm/yy
 *
 */
function checkCreditCard(CardName, CardNumber, Expiry) {
    //removing spaces
    var cardnumber = CardNumber.replace(/\s/g, "").replace(/-/g, "");
    //making card name lowercase
    var cardname = CardName.toLowerCase();
    if (checkLength(cardnumber, cardname) & checkNumbers(cardnumber) & checkExpired(Expiry) & calculateCheckSum(cardnumber)) {
        if (cardname == "visa") {
            if (cardnumber.substring(0, 1) == '4') {
                return true;
            }
        } else if (cardname == "mastercard") {
            if (cardnumber.substring(0, 2) == '51' || cardnumber.substring(0, 2) == '52' || cardnumber.substring(0, 2) == '53' ||
                cardnumber.substring(0, 2) == '54' || cardnumber.substring(0, 2) == '55') {
                return true;
            }
        } else if (cardname == "maestro") {
            if (cardnumber.substring(0, 4) == '5018' || cardnumber.substring(0, 4) == '5020' || cardnumber.substring(0, 4) == '5038' ||
                cardnumber.substring(0, 4) == '6304' || cardnumber.substring(0, 4) == '6759' || cardnumber.substring(0, 4) == '6761' ||
                cardnumber.substring(0, 4) == '6762' || cardnumber.substring(0, 4) == '6763') {
                return true;
            }
        } else if (cardname == "visaelectron") {
            if (cardnumber.substring(0, 4) == '4026' || cardnumber.substring(0, 4) == '4508' || cardnumber.substring(0, 4) == '4844' ||
                cardnumber.substring(0, 4) == '4913' || cardnumber.substring(0, 4) == '4917' || cardnumber.substring(0, 6) == '417500') {
                return true;
            }
        }
    }
    return false;
}


//checking that the length is correct
function checkLength(cardnumber, cardname) {
    if (cardname == "visa") {
        if (cardnumber.length == 16 || cardnumber.length == 13) {
            return true;
        }
    } else if (cardname == "mastercard" || cardname == "visaelectron") {
        if (cardnumber.length == 16) {
            return true;
        }
    } else if (cardname == "maestro") {
        if (cardnumber.length == 12 || cardnumber.length == 13 || cardnumber.length == 14 || cardnumber.length == 15 ||
            cardnumber.length == 16 || cardnumber.length == 18 || cardnumber.length == 19) {
            return true;
        }
    }
    return false;
}

//checking that the card is only made up of numbers
function checkNumbers(cardnumber) {
    var regex = /^[0-9]{13,19}$/;
    if (regex.exec(cardnumber)) {
        return true;
    }
    return false;
}

//checking that the card is not expired
function checkExpired(expiry) {
    var currentdate = new Date();
    var year = currentdate.getFullYear();
    var month = currentdate.getMonth();
    year = parseInt(year.toString().substring(2, 4));
    var cardYear = parseInt(expiry.substring(3, 5));
    var cardMonth = parseInt(expiry.substring(0, 2));
    if (year < cardYear) {
        return true;
    } else if (year == cardYear) {
        if (month + 1 <= cardMonth) {
            return true;
        }
    }
    return false;
}

//checking that the checksum is correct
function calculateCheckSum(cardnumber) {
    var sum = 0;
    var alternate = false;
    for (var i = cardnumber.length - 1; i >= 0; i--) {
        var number = parseInt(cardnumber.substring(i, i + 1));
        if (alternate) {
            number *= 2;
            if (number > 9) {
                number = (number % 10) + 1;
            }
        }
        sum += number;
        alternate = !alternate;
    }
    return sum % 10 == 0;
}