SELECT *
FROM (
select base_entity_id,
event_id,
event_date::TIMESTAMP,
'bleeding or blood soiling of dressing' as adverse_event,
'moderate' AS type_of_adverse_event,
health_facility,
district_council,
region
FROM
(SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'bleeding or blood soiling of dressing' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_bleeding ='pressure_dressing')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION ALL
SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'bleeding or blood soiling of dressing' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_bleed_excessive_bleeding = 'bleed_require_pressure_dressing')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
) AS ae
UNION
select base_entity_id,
event_id,
event_date::TIMESTAMP,
'bleeding or blood soiling of dressing' as adverse_event,
'severe' AS type_of_adverse_event,
health_facility,
district_council,
region
FROM
(SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'bleeding or blood soiling of dressing' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_bleeding ='blood_transfusion')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION ALL
SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'bleeding or blood soiling of dressing' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_bleed_excessive_bleeding = 'blood_transfussion_or_transfered_another_facility')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
) AS ae



UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'swelling of the penis or scrotum ' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_swelling ='pressure_dressing')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'bleeding or blood soiling of dressing' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_swelling ='penis_severed')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'persistent pain ' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_persistent_pain ='5_or_6_on_pain_scale')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'persistent pain ' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_persistent_pain ='7_on_pain_scale')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Infection ' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_infection ='purulent')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Infection ' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_infection ='cellutilis')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')



UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'failure to pass urine' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_pass_urine ='return_cinic')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'failure to pass urine ' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_pass_urine ='requires_referral')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')



UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'device detachment' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_device_detachment ='meet_severe_criteria')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION
SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'device detachment ' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_device_detachment ='surgical_intervention')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')




UNION
select base_entity_id,
event_id,
event_date::TIMESTAMP,
'Device displacement' as adverse_event,
'moderate' AS type_of_adverse_event,
health_facility,
district_council,
region
FROM
(SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Device displacement' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_device_displacement = 'requires_any_treatment_more_than_routine')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION ALL
SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Device displacement' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_of_adverse_event_device_displacement = 'requires_any_treatment_more_than_routine_post_operative_wound_care')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
) AS ae
UNION
select base_entity_id,
event_id,
event_date::TIMESTAMP,
'Device displacement' as adverse_event,
'severe' AS type_of_adverse_event,
health_facility,
district_council,
region
FROM
(SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Device displacement' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_event_device_displacement = 'intentional_movement_of_device_by_client')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION ALL
SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Device displacement' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_of_adverse_event_device_displacement = 'intentional_movement_of_device_by_client_or_self_removal_that_requires_surgical_intervention_hospitalization_transfer')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
) AS ae



UNION
select base_entity_id,
event_id,
event_date::TIMESTAMP,
'Excessive skin removed' as adverse_event,
'moderate' AS type_of_adverse_event,
health_facility,
district_council,
region
FROM
(SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Excessive skin removed' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_excessive_skin_removed = 'additional_operative')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION ALL
SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Excessive skin removed' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_skin_removal = 'skin_tight')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
) AS ae
UNION
select base_entity_id,
event_id,
event_date::TIMESTAMP,
'Excessive skin removed' as adverse_event,
'severe' AS type_of_adverse_event,
health_facility,
district_council,
region
FROM
(SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Excessive skin removed' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_excessive_skin_removed = 'operation_or_transfer_to_another_facility'
)
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
UNION ALL
SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Excessive skin removed' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_skin_removal = 'requires_reoperation')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
) AS ae



UNION
SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'AE Injury to penis' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_injury_to_penis ='bruising_or_abrasion_of_the_glans_or_shaft_of_the_penis')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_procedure.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'AE Injury to penis' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_injury_to_penis ='glans_or_shaft_of_penis_severed')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'AE Anaesthetic-related event' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_anaesthetic_related_event ='reaction_to_anaesthetic')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'AE Anaesthetic-related event' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_procedure
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_procedure.desc_intraoperative_ae_anaesthetic_related_event ='anaphylaxis')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Excessive skin removed' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_excessive_skin_removed ='additional_operative')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Excessive skin removed ' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_excessive_skin_removed ='operation_or_transfer_to_another_facility')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Delayed wound healing' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_delayed_wound_healing ='additional_non_operative_treatment')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Delayed wound healing ' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_delayed_wound_healing ='requires_re_operation')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Appearance' as adverse_event,
'moderate' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_appearance ='significant_wound_disruption_or_scarring_but_does_not_require_re_operation')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')

UNION

SELECT vmmc_followup_visit.base_entity_id,
vmmc_procedure.event_id,
vmmc_procedure.event_date::TIMESTAMP,
'Appearance ' as adverse_event,
'severe' AS type_of_adverse_event,
tanzania_locations.health_facility,
tanzania_locations.district_council,
tanzania_locations.region
FROM vmmc_followup_visit
inner join vmmc_procedure  ON vmmc_procedure.base_entity_id    = vmmc_followup_visit.base_entity_id
inner join public.tanzania_locations on tanzania_locations.location_uuid = vmmc_procedure.location_id
WHERE (vmmc_followup_visit.desc_of_post_op_adverse_appearance ='requires_re_operation')
AND (male_circumcision_method ='dorsal_slit' OR male_circumcision_method ='sleeve_resection' OR male_circumcision_method ='device')
) as whole