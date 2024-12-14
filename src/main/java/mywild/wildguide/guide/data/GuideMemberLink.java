package mywild.wildguide.guide.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table("guide_members")
public class GuideMemberLink  {

    @Id
    private long id;

    private long guideId;

    private long userId;

}
