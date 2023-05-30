package com.dopamines.backend.friend.entity;

import com.dopamines.backend.account.entity.Account;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column
    private Long friendId;
    @Column
    private String friendEmail;

    @ManyToOne
    @JsonBackReference
//    @JsonIgnoreProperties
    @JoinColumn(name="account_id")
//    @JsonIgnoreProperties({"friends", "friendWaitEntities"})
//    @NonNull
    private Account account;

    public static Friend toBuild(Account account,Account friend)
    {
        return Friend.builder()
//                .myId(myId)
                .friendId(friend.getAccountId())
                .friendEmail(friend.getEmail())
                .account(account)
                .build();
    }

    public static Friend checkExistsInFriends(List<Friend> friendList, Long accountId)
    {
        for(Friend friend : friendList)
        {
            if(friend.getFriendId()==accountId)
            {
                return friend;
            }
        }
        return null;
    }
}
