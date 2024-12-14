package mywild.wildguide.guide.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
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
@Table("guide_owners")
public class GuideOwnerLink {

    @Id
    private long id;

    private long guideId;

    private long userId;

}
