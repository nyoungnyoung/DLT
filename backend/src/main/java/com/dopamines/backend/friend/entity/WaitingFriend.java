package com.dopamines.backend.friend.entity;

import com.dopamines.backend.account.entity.Account;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class WaitingFriend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column
    private Long friendId;
    @Column
    private String friendEmail;

    @ManyToOne
    @JoinColumn(name="account_id")
    @NonNull
    private Account account;

    public static WaitingFriend toBuild(Account account, Account friend)
    {
        return WaitingFriend.builder()
//                .myId(myId)
                .friendId(friend.getAccountId())
                .friendEmail(friend.getEmail())
                .account(account)
                .build();

    }
    public static WaitingFriend checkExistsInWaitList(List<WaitingFriend> waitingFriendList, Long accountId)
    {
        for(WaitingFriend waitingFriend : waitingFriendList)
        {
            if(waitingFriend.getFriendId()==accountId)
            {
                return waitingFriend;
            }
        }
        return null;
    }
}
