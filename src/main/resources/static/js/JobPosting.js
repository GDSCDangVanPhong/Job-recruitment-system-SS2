
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    document.addEventListener('DOMContentLoaded', function () {

    const editButtons = document.querySelectorAll('.btn-edit');
    let postedDate = "";
    let jobPostingId = "";
    editButtons.forEach(button => {
    button.addEventListener('click', function () {
    jobPostingId = button.getAttribute('data-id');
    const jobTitle = button.getAttribute('data-title');
    const jobType = button.getAttribute('data-jobtype');
    const location = button.getAttribute('data-location');

    const experience = button.getAttribute('data-experience');
    const salary = button.getAttribute('data-salary');
    const description = button.getAttribute('data-description');
    const requirements = button.getAttribute('data-requirements');
    const companyName = button.getAttribute('company-name');
    const deadline = button.getAttribute('data-deadline');
    const industryId = button.getAttribute('data-industry-id');
    const benefit = button.getAttribute('benefit');
    postedDate = button.getAttribute('data-posted-date');

    document.getElementById('editJobId').value = jobPostingId;
    document.getElementById('editTitle').value = jobTitle;
    document.getElementById('editJobType').value = jobType;
    document.getElementById('editLocation').value = location;
    document.getElementById('editCompany').value = companyName;
    document.getElementById('editExperience').value = experience;
    document.getElementById('editSalary').value = salary;
    document.getElementById('editDescription').value = description;
    document.getElementById('editDeadline').value = deadline;
    document.getElementById('editBenefit').value = benefit;

    const industrySelect = document.getElementById('editCategory');
    Array.from(industrySelect.options).forEach(option => {
    option.selected = option.value === industryId;
});
    // Điền yêu cầu công việc
    const requirementsArray = requirements.split(',');

    const requirementsContainer = document.getElementById('editRequirementsContainer');
    requirementsContainer.innerHTML = ''; // Clear existing requirements

    requirementsArray.forEach(req => {
    req = req.replace(/^\[|\]$/g, '').trim();
    req = req.charAt(0).toUpperCase() + req.slice(1);
    const requirementDiv = document.createElement('div');
    requirementDiv.classList.add('input-group', 'mb-2');
    requirementDiv.innerHTML = `
          <input type="text" class="form-control" name="requirements[]" value="${req}" required>
          <button type="button" class="btn btn-danger remove-requirement">-</button>
        `;
    requirementsContainer.appendChild(requirementDiv);

    requirementDiv.querySelector('.remove-requirement').addEventListener('click', function () {
    requirementsContainer.removeChild(requirementDiv);
});
});
});
});
    const addRequirementButton = document.getElementById('add-requirement');
    if (addRequirementButton) {
    addRequirementButton.addEventListener('click', function () {
    const requirementsContainer = document.getElementById('editRequirementsContainer');
    const requirementDiv = document.createElement('div');
    requirementDiv.classList.add('input-group', 'mb-2');
    requirementDiv.innerHTML = `
        <input type="text" class="form-control" name="requirements[]" placeholder="Enter requirement" required>
        <button type="button" class="btn btn-danger remove-requirement">-</button>
      `;
    requirementsContainer.appendChild(requirementDiv);

    // Xử lý sự kiện xóa yêu cầu
    requirementDiv.querySelector('.remove-requirement').addEventListener('click', function () {
    requirementsContainer.removeChild(requirementDiv);
});
});
}
    const editJobForm = document.getElementById('editJobForm');
    if (editJobForm) {
    editJobForm.addEventListener('submit', function (event) {
    event.preventDefault();
    // Lấy ngày đăng tin và ngày hết hạn ứng tuyển
    const postedDateObj = new Date(postedDate);  // Chuyển đổi postedDate thành đối tượng Date
    const applicationDeadline = document.getElementById('editDeadline').value;  // Lấy giá trị ngày hết hạn
    const applicationDeadlineObj = new Date(applicationDeadline);  // Chuyển đổi ngày hết hạn thành đối tượng Date

    // Tính khoảng cách giữa ngày đăng tin và ngày hết hạn ứng tuyển
    const timeDifference = applicationDeadlineObj - postedDateObj;
    const dayDifference = timeDifference / (1000 * 3600 * 24);  // Chuyển đổi từ mili giây sang ngày

    // Kiểm tra nếu khoảng cách giữa ngày đăng tin và ngày hết hạn ứng tuyển < 7 ngày
    if (dayDifference < 7) {
    alert("The application deadline must be at least 7 days after the job posting date.");
    return;  // Ngừng gửi form
}

    // Lấy dữ liệu từ form
    const formData = new FormData(editJobForm);
    const data = {};

    let isValid = true; // Biến để kiểm tra tính hợp lệ của form

    // Duyệt qua tất cả các trường trong form để kiểm tra tính hợp lệ
    formData.forEach((value, key) => {
    if (key === "requirements[]") {
    if (!data.requirements) {
    data.requirements = [];
}
    data.requirements.push(value);
} else {
    // Kiểm tra nếu trường này là rỗng
    if (!value.trim()) {
    isValid = false; // Nếu có trường nào rỗng, đánh dấu form không hợp lệ
    alert(`${key} cannot be empty!`); // Thông báo cho người dùng
}
    data[key] = value;
}
});

    if (data.requirements && data.requirements.length > 0) {
    data.requirements = data.requirements.join(", ");
}
    // Kiểm tra nếu form không hợp lệ, dừng việc gửi form
    if (!isValid) {
    return;
}
    console.log('Data being sent to server:', data);
    // Nếu form hợp lệ, gửi yêu cầu PUT
    fetch(`/company/update-job-posting/${jobPostingId}`, {
    method: 'PUT',
    headers: {
    'X-CSRF-TOKEN': csrfToken,
    'Content-Type': 'application/json'
},
    body: JSON.stringify(data),
})
    .then(response => response.json())
    .then(data => {
    if (data.status === 'success') {
    alert('Job posting updated successfully!');
    window.location.reload(); // Tải lại trang để thấy thay đổi
} else {
    alert('Error updating job posting: ' + data.message);
}
})
    .catch(error => {
    console.error('Error:', error);
    alert('An error occurred while updating the job posting.');
});
});
}

});
    function deleteJob(element) {

    const jobId = element.getAttribute('data-id');

    // Xác nhận từ người dùng
    const userConfirmed = confirm('Are you sure you want to delete this job posting?');
    if (!userConfirmed) {
    return false; // Người dùng không xác nhận, không thực hiện gì thêm
}

    // Gửi yêu cầu xóa bằng fetch
    fetch(`/company/delete-job-posting/${jobId}`, {
    method: 'DELETE', // Sử dụng phương thức DELETE
    headers: {
    'X-CSRF-TOKEN': csrfToken,
    'Content-Type': 'application/json', // Nếu cần, định dạng nội dung
},
})
    .then(response => {
    if (response.ok) {
    const jobCard = element.closest('.job-card'); // Tìm phần tử cha chứa job card
    if (jobCard) {
    jobCard.remove(); // Xóa khỏi DOM
}
    alert('Job deleted successfully!');
} else {
    alert('Failed to delete the job posting.');
}
})
    .catch(error => {
    console.error('Error deleting job:', error);
    alert('An error occurred while deleting the job posting.');
});

    return false; // Ngăn trình duyệt chuyển hướng do href="#"
}

    function updateJobStatus(selectElement) {
    // Lấy giá trị trạng thái đã chọn từ dropdown
    var newStatus = selectElement.value;
    var id = selectElement.getAttribute('data-id');
    console.log("jobPostingId:", id);
    console.log("newStatus:", newStatus);
    // Gửi yêu cầu cập nhật trạng thái bằng fetch
    fetch(`/company/update-job-posting-status/${id}/${newStatus}`, {
    method: 'PUT',
    headers: {
    'X-CSRF-TOKEN': csrfToken,  // CSRF Token nếu có
    'Content-Type': 'application/json'
}
})
    .then(response => {

    if (response.ok) {
    response.json().then(data => {

    const jobStatus = data.status;
    const jobId = data.id;

    const spans = document.querySelectorAll('span[data-id]');

    spans.forEach(span => {
    if (span.getAttribute('data-id') === String(jobId)) {

    switch (jobStatus) {
    case 'OPEN':
    span.textContent = 'Open';
    span.className = 'badge bg-success';
    break;
    case 'CLOSED':
    span.textContent = 'Closed';
    span.className = 'badge bg-danger'; // Cập nhật lớp CSS
    break;
    case 'EXPIRED':
    span.textContent = 'Expired';
    span.className = 'badge bg-warning'; // Cập nhật lớp CSS
    break;
    case 'FILLED':
    span.textContent = 'Filled';
    span.className = 'badge bg-secondary'; // Cập nhật lớp CSS
    break;
    default:
    span.textContent = 'Unknown';
    span.className = 'badge bg-info'; // Cập nhật lớp CSS mặc định
    break;
}
}

});
})
} else {
    throw new Error('Error updating job status');
}
})
    .then(data => {
    console.log("Job status updated successfully.");
    // Có thể cập nhật giao diện người dùng nếu cần
})
        .catch(error => {
            console.error(error);

        });
}
