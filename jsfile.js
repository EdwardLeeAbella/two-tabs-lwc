import { LightningElement, track, wire, api } from 'lwc';
import getaccountList from '@salesforce/apex/RecentlyViewAccountController.getaccountList';
import { NavigationMixin } from "lightning/navigation";

const account_col = [
    {
        label: "Account Name",
        type: "button",
        typeAttributes: { label: { fieldName: "Name" }, name: "goToAccounts", variant: "base" }
    },
    {
        label: "Country",
        fieldName: "Country_GCS_Shipping_Country__c"
    },
    {
        label: "Ultimate Parent Account",
        fieldName: "Ultimate_Parent__c"
    },
    {
        label: "Industry",
        fieldName: "Industry_LGS__c"
    },
    {
        label: "Website",
        fieldName: "Website"
    }
];

export default class RecentlyViewAccount extends NavigationMixin(LightningElement) {
    @api Channel = 'Recently Viewed Accounts';
    accountColumns = account_col;
    error;
    @track lstAccounts = [];


    connectedCallback(){
        getaccountList({channel: this.Channel})
        .then(result => {            
            this.lstAccounts = result;
        })
        .catch(error => {
            console.log('Error', error);
        });
    }


    handleRowAction(event) {
        if (event.detail.action.name === "goToAccounts") {
            this[NavigationMixin.GenerateUrl]({
                type: "standard__recordPage",
                attributes: {
                    recordId: event.detail.row.Id,
                    actionName: "view"
                }
            }).then((url) => {
                window.open(url, "_self");
            });
        }
    }
}
