public with sharing class RecentlyViewAccountController {
    @AuraEnabled(cacheable=true)
    public static List<Account> getaccountList(string channel) {

        // The main account list to return
        List<Account> accountListToRender = new List<Account>();

        Set<string> setRecordTypeName = new Set<string>();
        setRecordTypeName.add('Location');
        setRecordTypeName.add('LGS Client Country');

        Set<Id> recentIdSet = new Set<Id>();
        for (RecentlyViewed recent: [SELECT Id, Name, RecordType.SobjectType, RecordType.Name FROM RecentlyViewed 
                                    WHERE RecordType.SobjectType = 'Account' AND RecordType.Name IN: setRecordTypeName LIMIT 20]) {
            recentIdSet.add(recent.Id);
        }

        Set<Id> accountIdSet = new Set<Id>();
        for(Account accts: [SELECT Id, ParentId, RecordType.Name, LastViewedDate FROM Account WHERE Id IN: recentIdSet ORDER BY LastViewedDate DESC]) {
            if(accts.RecordType.Name == 'LGS Client Country'){
                accountIdSet.add(accts.ParentId);
            } else if(accts.RecordType.Name == 'Location'){
                accountIdSet.add(accts.Id);
            }
        }
        List<Account> parentAccountList = [SELECT Id, Name, Country_GCS_Shipping_Country__c, Ultimate_Parent__c, Industry_LGS__c, Website, LastViewedDate 
                                        FROM Account WHERE Id IN: accountIdSet ORDER BY LastViewedDate DESC];

        // For the Owned Accounts
        Id currentUser = UserInfo.getUserId();
        List<Account> ownedAccountList = [SELECT Id, Name, Country_GCS_Shipping_Country__c, RecordType.Name, Primary_Account_Owner__c, Secondary_Account_Owner__c, X3rd_Account_Owner__c, Ultimate_Parent__c, Industry_LGS__c, Website 
                                            FROM Account WHERE RecordType.Name = 'Location' AND 
                                            (Primary_Account_Owner__c =:currentUser OR Secondary_Account_Owner__c  =:currentUser OR X3rd_Account_Owner__c =:currentUser)];

        if(channel == 'Recently Viewed Accounts') {
            for (Account accounts: parentAccountList) {
                accountListToRender.add(accounts);
            }
        } else {
            for (Account accounts: ownedAccountList) {
                accountListToRender.add(accounts);
            }
        }

        return accountListToRender;
    }

}
