<template>
  <div v-if="visible" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
    <!-- Modal -->
    <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true" ref="modalRef">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit User</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveEdit">
              <div class="mb-3">
                <label class="form-label">Name</label>
                <input v-model="formData.username" class="form-control" />
              </div>
              <div class="mb-3">
                <label class="form-label">Password</label>
                <input v-model="formData.password" class="form-control" />
              </div>
			  <div class="mb-3">
				  <label class="form-label">ROLE</label>
					<select
					  v-model="formData.roles"
					  class="form-control"
					>
					  <option disabled value="viewer">-- select role --</option>
					  <option v-for="role in roles" :key="role" :value="'ROLE_'+role">
						{{ role }}
					  </option>
					</select>
			  </div>
              <button type="submit" class="btn btn-success">Save</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: 'Form nhập liệu' },
  submitText: { type: String, default: 'Lưu' },
})

const emit = defineEmits(['close', 'submit'])

const roles = ['ADMIN', 'USER']

const formData = reactive({
  username: '',
  password: '',
  role: ''
})

const handleSubmit = () => {
  emit('submit', { ...formData })
  // reset form sau khi submit
  formData.username = ''
  formData.password = ''
  formData.role = ''
}
</script>
