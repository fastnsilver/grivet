package com.fns.grivet.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Profile("secure")
@RestController
public class MeController {

    @RequestMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountInfo info(HttpServletRequest req) {
        Account account = AccountResolver.INSTANCE.getAccount(req);
        return new AccountInfo(account.getHref(), account.getFullName(), account.getEmail());
    }

    @JsonPropertyOrder(value = { "href", "fullName", "email " })
    class AccountInfo {

        private String href;
        private String fullName;
        private String email;

        @JsonCreator
        public AccountInfo(@JsonProperty String href, @JsonProperty String fullName, @JsonProperty String email) {
            this.href = href;
            this.fullName = fullName;
            this.email = email;
        }

        public String getHref() {
            return href;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

    }
}
