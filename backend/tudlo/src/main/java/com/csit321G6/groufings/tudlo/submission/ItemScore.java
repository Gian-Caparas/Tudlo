package com.csit321G6.groufings.tudlo.submission;

import com.csit321G6.groufings.tudlo.assessment.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_score")
@Getter
@Setter
public class ItemScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private AssessmentItem item;

    @Column(name = "points_earned")
    private Double pointsEarned;

    @Column(name = "is_missing", nullable = false)
    private boolean missing;
}
